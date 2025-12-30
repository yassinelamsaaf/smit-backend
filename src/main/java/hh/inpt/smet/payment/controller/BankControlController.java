package hh.inpt.smet.payment.controller;

import hh.inpt.smet.payment.service.BankService;
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

    public BankControlController(BankService bankService) {
        this.bankService = bankService;
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
        return ResponseEntity.ok(Map.of(
            "bankAvailable", true,
            "message", "Bank service ENABLED - payments will succeed directly"
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
        return ResponseEntity.ok(Map.of(
            "bankAvailable", newStatus,
            "message", newStatus ?
                "Bank service ENABLED - payments will succeed directly" :
                "Bank service DISABLED - payments will be queued to Kafka"
        ));
    }
}
