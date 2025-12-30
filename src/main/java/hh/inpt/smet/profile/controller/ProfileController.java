package hh.inpt.smet.profile.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hh.inpt.smet.payment.model.BankAccount;
import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.model.TelecomAccount;
import hh.inpt.smet.profile.model.Profile;
import hh.inpt.smet.profile.service.ProfileService;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService service;

    public ProfileController(ProfileService service) {
        this.service = service;
    }

    @GetMapping
    public List<Profile> list() {
        return service.listAll();
    }

    @GetMapping("/touriste/{id}")
    public Profile forTouriste(@PathVariable Long id) {
        return service.getProfileForTouriste(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Profile p) {
        return ResponseEntity.badRequest().body("Create profiles via Touriste creation");
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<Profile> updateMyPreferences(@RequestBody hh.inpt.smet.preferences.model.HotelPreference preferences) {
        Profile updated = service.updatePreferencesForCurrentUser(preferences);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/payments/bank")
    public ResponseEntity<?> addBankToMe(@RequestBody BankAccount b) {
        PaymentMethod saved = service.addPaymentMethodForCurrentUser(b);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/me/payments/telecom")
    public ResponseEntity<?> addTelecomToMe(@RequestBody TelecomAccount t) {
        PaymentMethod saved = service.addPaymentMethodForCurrentUser(t);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
