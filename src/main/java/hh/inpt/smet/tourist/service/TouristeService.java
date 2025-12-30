package hh.inpt.smet.tourist.service;

import hh.inpt.smet.tourist.model.TouristeEntity;
import hh.inpt.smet.tourist.persistence.TouristeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TouristeService {

    private final TouristeRepository repo;

    public TouristeService(TouristeRepository repo) {
        this.repo = repo;
    }

    public List<TouristeEntity> findAll() {
        return repo.findAll();
    }

    public TouristeEntity findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Touriste not found"));
    }

    public TouristeEntity save(TouristeEntity t) {
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
