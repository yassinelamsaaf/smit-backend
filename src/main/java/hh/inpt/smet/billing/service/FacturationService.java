package hh.inpt.smet.billing.service;

import hh.inpt.smet.billing.dto.PaymentEvent;
import hh.inpt.smet.billing.model.Facturation;
import hh.inpt.smet.billing.persistence.FacturationRepository;
import hh.inpt.smet.payment.model.BankAccount;
import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.service.BankService;
import hh.inpt.smet.tourist.persistence.TouristeRepository;
import hh.inpt.smet.tourist.model.TouristeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturationService {

    private static final Logger logger = LoggerFactory.getLogger(FacturationService.class);

    private final FacturationRepository repo;
    private final TouristeRepository touristeRepository;
    private final BankService bankService;
    private final PaymentStreamProducer streamProducer;

    public FacturationService(FacturationRepository repo,
                              TouristeRepository touristeRepository,
                              BankService bankService,
                              PaymentStreamProducer streamProducer) {
        this.repo = repo;
        this.touristeRepository = touristeRepository;
        this.bankService = bankService;
        this.streamProducer = streamProducer;
    }

    public List<Facturation> findAll() {
        return repo.findAll();
    }

    public Facturation findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Facturation not found"));
    }

    @Transactional
    public Facturation pay(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TouristeEntity touriste = touristeRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Facturation f = findById(id);
        if (!f.getTouristeId().equals(touriste.getId())) {
            throw new RuntimeException("Facture does not belong to authenticated user");
        }
        if (!"PENDING".equals(f.getStatutPaiement())) {
            throw new RuntimeException("Facture is not in PENDING status");
        }

        PaymentMethod paymentMethod = f.getPaymentMethod();

        // If payment method is a bank account, try direct payment first
        if (paymentMethod instanceof BankAccount) {
            BankAccount bankAccount = (BankAccount) paymentMethod;
            logger.info("Attempting bank payment for facture {}", f.getId());

            boolean bankPaymentSuccess = bankService.processBankPayment(
                bankAccount.getIban(),
                f.getMontantTotal()
            );

            if (bankPaymentSuccess) {
                // Direct payment succeeded
                logger.info("Bank payment successful for facture {}", f.getId());
                f.setStatutPaiement("PAID");
                return repo.save(f);
            } else {
                // Bank unavailable - send to Spring Cloud Stream
                logger.warn("Bank unavailable for facture {}. Sending to stream queue.", f.getId());

                PaymentEvent event = PaymentEvent.builder()
                    .factureId(f.getId())
                    .paymentMethodId(paymentMethod.getId())
                    .paymentType("BANK")
                    .amount(f.getMontantTotal())
                    .touristeId(f.getTouristeId())
                    .retryCount(0)
                    .build();

                streamProducer.sendPaymentEvent(event);

                // Update status to QUEUED
                f.setStatutPaiement("QUEUED");
                return repo.save(f);
            }
        } else {
            // Non-bank payment methods (Telecom, etc.) - process directly
            logger.info("Processing non-bank payment for facture {}", f.getId());
            f.setStatutPaiement("PAID");
            return repo.save(f);
        }
    }

    @Transactional
    public int requeueQueuedBankPayments() {
        List<Facturation> queuedFactures = repo.findByStatutPaiement("QUEUED");
        int requeuedCount = 0;

        for (Facturation facture : queuedFactures) {
            PaymentMethod paymentMethod = facture.getPaymentMethod();
            if (!(paymentMethod instanceof BankAccount)) {
                continue;
            }

            PaymentEvent event = PaymentEvent.builder()
                .factureId(facture.getId())
                .paymentMethodId(paymentMethod.getId())
                .paymentType("BANK")
                .amount(facture.getMontantTotal())
                .touristeId(facture.getTouristeId())
                .retryCount(0)
                .build();

            streamProducer.sendPaymentEvent(event);
            requeuedCount++;
        }

        return requeuedCount;
    }
}
