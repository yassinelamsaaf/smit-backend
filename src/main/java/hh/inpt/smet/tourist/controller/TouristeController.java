package hh.inpt.smet.tourist.controller;

import hh.inpt.smet.tourist.dto.TouristeDTO;
import hh.inpt.smet.tourist.model.TouristeEntity;
import hh.inpt.smet.tourist.service.TouristeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/touristes")
public class TouristeController {

    private final TouristeService service;

    public TouristeController(TouristeService service) {
        this.service = service;
    }

    @GetMapping
    public List<TouristeDTO> list() {
        return service.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TouristeDTO get(@PathVariable Long id) {
        return toDto(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<TouristeDTO> create(@RequestBody TouristeEntity t) {
        TouristeEntity saved = service.save(t);
        return ResponseEntity.ok(toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private TouristeDTO toDto(TouristeEntity e) {
        return TouristeDTO.builder()
                .id(e.getId())
                .username(e.getUsername())
                .email(e.getEmail())
                .nom(e.getNom())
                .prenom(e.getPrenom())
                .telephone(e.getTelephone())
                .profile(e.getProfile())
                .build();
    }
}
