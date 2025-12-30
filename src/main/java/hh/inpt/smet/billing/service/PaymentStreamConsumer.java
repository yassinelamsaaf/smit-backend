package hh.inpt.smet.billing.service;

import hh.inpt.smet.billing.dto.PaymentEvent;
import hh.inpt.smet.billing.model.Facturation;
import hh.inpt.smet.billing.persistence.FacturationRepository;
import hh.inpt.smet.payment.model.BankAccount;
import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.persistence.PaymentMethodRepository;
import hh.inpt.smet.payment.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Spring Cloud Stream consumer for processing payment events from Kafka
 */
@Configuration
public class PaymentStreamConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentStreamConsumer.class);
    private static final int MAX_RETRY_COUNT = 3;

    private final FacturationRepository facturationRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final BankService bankService;
    private final PaymentStreamProducer streamProducer;

    public PaymentStreamConsumer(FacturationRepository facturationRepository,
                                 PaymentMethodRepository paymentMethodRepository,
                                 BankService bankService,
                                 PaymentStreamProducer streamProducer) {
        this.facturationRepository = facturationRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.bankService = bankService;
        this.streamProducer = streamProducer;
    }

    /**
     * Consumer bean for processing payment events.
     * Spring Cloud Stream will automatically bind this to the configured Kafka topic.
     */
    @Bean
    public Consumer<PaymentEvent> paymentConsumer() {
        return event -> processPaymentEvent(event);
    }

    @Transactional
    protected void processPaymentEvent(PaymentEvent event) {
        logger.info("Received payment event from stream: factureId={}, retryCount={}",
                   event.getFactureId(), event.getRetryCount());

        try {
            if (!bankService.getBankAvailable()) {
                logger.warn("Bank is disabled. Skipping stream processing for factureId={}.",
                           event.getFactureId());
                return;
            }

            // Retrieve facture
            Facturation facture = facturationRepository.findById(event.getFactureId())
                .orElseThrow(() -> new RuntimeException("Facture not found: " + event.getFactureId()));

            // Check if already paid
            if ("PAID".equals(facture.getStatutPaiement())) {
                logger.info("Facture {} already paid. Skipping.", event.getFactureId());
                return;
            }

            // Retrieve payment method
            PaymentMethod paymentMethod = paymentMethodRepository.findById(event.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found: " + event.getPaymentMethodId()));

            boolean paymentSuccess = false;

            // Process bank payment
            if (paymentMethod instanceof BankAccount) {
                BankAccount bankAccount = (BankAccount) paymentMethod;
                paymentSuccess = bankService.processBankPayment(bankAccount.getIban(), event.getAmount());
            } else {
                logger.warn("Payment method type not supported for stream processing: {}",
                           paymentMethod.getClass().getSimpleName());
                return;
            }

            if (paymentSuccess) {
                // Update facture status to PAID
                facture.setStatutPaiement("PAID");
                facturationRepository.save(facture);
                logger.info("Payment processed successfully via stream: factureId={}", event.getFactureId());
            } else {
                // Retry logic
                handlePaymentFailure(event, facture);
            }

        } catch (Exception e) {
            logger.error("Error processing payment event: factureId={}, error={}",
                        event.getFactureId(), e.getMessage());
            // Could implement dead letter queue here
        }
    }

    private void handlePaymentFailure(PaymentEvent event, Facturation facture) {
        int currentRetry = event.getRetryCount() != null ? event.getRetryCount() : 0;

        if (currentRetry < MAX_RETRY_COUNT) {
            // Retry by sending back to stream
            logger.warn("Payment failed for factureId={}. Retrying ({}/{})",
                       event.getFactureId(), currentRetry + 1, MAX_RETRY_COUNT);

            event.setRetryCount(currentRetry + 1);
            streamProducer.sendPaymentEvent(event);
        } else {
            // Max retries reached - mark as FAILED
            logger.error("Payment failed after {} retries for factureId={}. Marking as FAILED.",
                        MAX_RETRY_COUNT, event.getFactureId());
            facture.setStatutPaiement("FAILED");
            facturationRepository.save(facture);
        }
    }
}
