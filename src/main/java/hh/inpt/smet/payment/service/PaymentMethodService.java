package hh.inpt.smet.payment.service;

import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.persistence.PaymentMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repo;

    public PaymentMethodService(PaymentMethodRepository repo) {
        this.repo = repo;
    }

    public List<PaymentMethod> findAll() {
        return repo.findAll();
    }

    public PaymentMethod findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Payment method not found"));
    }

    public PaymentMethod save(PaymentMethod pm) {
        return repo.save(pm);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
