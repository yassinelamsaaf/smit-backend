package hh.inpt.smet.billing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.inpt.smet.billing.model.Facturation;
import hh.inpt.smet.billing.service.FacturationService;

@RestController
@RequestMapping("/api/factures")
public class FacturationController {

    private final FacturationService service;

    public FacturationController(FacturationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Facturation> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Facturation get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<Facturation> pay(@PathVariable Long id) {
        Facturation paid = service.pay(id);
        return ResponseEntity.ok(paid);
    }

    // Support alternate URL pattern: /api/factures/{id}/pay
    @PostMapping("/{id}/pay")
    public ResponseEntity<Facturation> payAlt(@PathVariable Long id) {
        Facturation paid = service.pay(id);
        return ResponseEntity.ok(paid);
    }
}
