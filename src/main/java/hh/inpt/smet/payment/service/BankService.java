package hh.inpt.smet.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Mock service to simulate bank payment processing.
 * For academic purposes - simulates random bank availability.
 */
@Service
public class BankService {

    private static final Logger logger = LoggerFactory.getLogger(BankService.class);
    private final Random random = new Random();

    // Toggle this to simulate bank availability (true = available, false = down)
    private boolean bankAvailable = false;

    /**
     * Check if the bank service is currently available.
     * Simulates random availability for testing purposes.
     */
    public boolean isBankAvailable() {
        // Simulate 70% availability rate for testing
        boolean available = bankAvailable && random.nextDouble() < 0.7;
        logger.info("Bank availability check: {}", available ? "AVAILABLE" : "UNAVAILABLE");
        return available;
    }

    /**
     * Process a bank payment.
     * @param iban The bank account IBAN
     * @param amount The amount to charge
     * @return true if payment succeeded, false otherwise
     */
    public boolean processBankPayment(String iban, Double amount) {
        if (!isBankAvailable()) {
            logger.warn("Bank payment FAILED - Service unavailable. IBAN: {}, Amount: {}", iban, amount);
            return false;
        }

        // Simulate payment processing delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("Bank payment SUCCESSFUL - IBAN: {}, Amount: {}", iban, amount);
        return true;
    }

    /**
     * Manual toggle for testing - allows enabling/disabling bank service
     */
    public void setBankAvailable(boolean available) {
        this.bankAvailable = available;
        logger.info("Bank service manually set to: {}", available ? "AVAILABLE" : "UNAVAILABLE");
    }

    public boolean getBankAvailable() {
        return this.bankAvailable;
    }
}
