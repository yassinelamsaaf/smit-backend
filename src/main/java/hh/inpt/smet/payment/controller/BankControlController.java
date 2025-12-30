package hh.inpt.smet.payment.controller;

import hh.inpt.smet.payment.service.BankService;
import hh.inpt.smet.billing.service.FacturationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for testing - allows toggling bank availability
 */
@RestController
@RequestMapping("/api/bank-control")
public class BankControlController {

    private final BankService bankService;
    private final FacturationService facturationService;

    public BankControlController(BankService bankService, FacturationService facturationService) {
        this.bankService = bankService;
        this.facturationService = facturationService;
    }

    /**
     * Get current bank availability status
     * GET /api/bank-control/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getBankStatus() {
        return ResponseEntity.ok(Map.of(
            "bankAvailable", bankService.getBankAvailable(),
            "message", bankService.getBankAvailable() ?
                "Bank is AVAILABLE - payments will succeed directly" :
                "Bank is UNAVAILABLE - payments will be queued to Kafka"
        ));
    }

    /**
     * Enable bank service (payments will succeed)
     * POST /api/bank-control/enable
     */
    @PostMapping("/enable")
    public ResponseEntity<Map<String, Object>> enableBank() {
        bankService.setBankAvailable(true);
        int requeuedCount = facturationService.requeueQueuedBankPayments();
        return ResponseEntity.ok(Map.of(
            "bankAvailable", true,
            "requeuedCount", requeuedCount,
            "message", "Bank service ENABLED - queued payments were re-sent to Kafka"
        ));
    }

    /**
     * Disable bank service (payments will go to Kafka queue)
     * POST /api/bank-control/disable
     */
    @PostMapping("/disable")
    public ResponseEntity<Map<String, Object>> disableBank() {
        bankService.setBankAvailable(false);
        return ResponseEntity.ok(Map.of(
            "bankAvailable", false,
            "message", "Bank service DISABLED - payments will be queued to Kafka"
        ));
    }

    /**
     * Toggle bank availability
     * POST /api/bank-control/toggle
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleBank() {
        boolean newStatus = !bankService.getBankAvailable();
        bankService.setBankAvailable(newStatus);
        int requeuedCount = newStatus ? facturationService.requeueQueuedBankPayments() : 0;
        return ResponseEntity.ok(Map.of(
            "bankAvailable", newStatus,
            "requeuedCount", requeuedCount,
            "message", newStatus ?
                "Bank service ENABLED - queued payments were re-sent to Kafka" :
                "Bank service DISABLED - payments will be queued to Kafka"
        ));
    }
}
