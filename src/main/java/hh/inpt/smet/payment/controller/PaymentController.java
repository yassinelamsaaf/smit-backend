package hh.inpt.smet.payment.controller;

import hh.inpt.smet.payment.dto.PaymentDTO;
import hh.inpt.smet.payment.model.BankAccount;
import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.model.TelecomAccount;
import hh.inpt.smet.payment.service.PaymentMethodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentMethodService service;

    public PaymentController(PaymentMethodService service) {
        this.service = service;
    }

    @GetMapping
    public List<PaymentDTO> list() {
        return service.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping("/bank")
    public ResponseEntity<PaymentDTO> addBank(@RequestBody BankAccount b) {
        PaymentMethod saved = service.save(b);
        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping("/telecom")
    public ResponseEntity<PaymentDTO> addTelecom(@RequestBody TelecomAccount t) {
        PaymentMethod saved = service.save(t);
        return ResponseEntity.ok(toDto(saved));
    }

    private PaymentDTO toDto(PaymentMethod pm) {
        if (pm instanceof BankAccount) {
            BankAccount b = (BankAccount) pm;
            return PaymentDTO.builder().id(b.getId()).estParDefaut(b.isEstParDefaut()).type("BANK").details("IBAN:" + b.getIban()).build();
        } else if (pm instanceof TelecomAccount) {
            TelecomAccount t = (TelecomAccount) pm;
            return PaymentDTO.builder().id(t.getId()).estParDefaut(t.isEstParDefaut()).type("TELECOM").details("MSISDN:" + t.getNumeroMobile()).build();
        }
        return PaymentDTO.builder().id(pm.getId()).estParDefaut(pm.isEstParDefaut()).type("UNKNOWN").details("").build();
    }
}
