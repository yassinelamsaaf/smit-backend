package hh.inpt.smet.billing.service;

import hh.inpt.smet.billing.model.Facturation;
import hh.inpt.smet.billing.persistence.FacturationRepository;
import hh.inpt.smet.tourist.persistence.TouristeRepository;
import hh.inpt.smet.tourist.model.TouristeEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturationService {

    private final FacturationRepository repo;
    private final TouristeRepository touristeRepository;

    public FacturationService(FacturationRepository repo, TouristeRepository touristeRepository) {
        this.repo = repo;
        this.touristeRepository = touristeRepository;
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
        TouristeEntity touriste = touristeRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Facturation f = findById(id);
        if (!f.getTouristeId().equals(touriste.getId())) {
            throw new RuntimeException("Facture does not belong to authenticated user");
        }
        if (!"PENDING".equals(f.getStatutPaiement())) {
            throw new RuntimeException("Facture is not in PENDING status");
        }
        f.setStatutPaiement("PAID");
        return repo.save(f);
    }
}
