package hh.inpt.smet.billing.service;

import hh.inpt.smet.billing.dto.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

/**
 * Spring Cloud Stream producer for sending payment events to Kafka
 */
@Service
public class PaymentStreamProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentStreamProducer.class);
    private static final String BINDING_NAME = "paymentProducer-out-0";

    private final StreamBridge streamBridge;

    public PaymentStreamProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    /**
     * Send a payment event to the Kafka stream for later processing
     */
    public void sendPaymentEvent(PaymentEvent event) {
        logger.info("Sending payment event to stream: factureId={}, amount={}",
                   event.getFactureId(), event.getAmount());

        boolean sent = streamBridge.send(BINDING_NAME, event);

        if (sent) {
            logger.info("Payment event sent successfully: factureId={}", event.getFactureId());
        } else {
            logger.error("Failed to send payment event: factureId={}", event.getFactureId());
        }
    }
}
