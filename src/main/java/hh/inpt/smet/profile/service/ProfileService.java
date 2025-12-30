package hh.inpt.smet.profile.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.payment.service.PaymentMethodService;
import hh.inpt.smet.preferences.model.Preferences;
import hh.inpt.smet.profile.model.Profile;
import hh.inpt.smet.tourist.model.TouristeEntity;
import hh.inpt.smet.tourist.persistence.TouristeRepository;

@Service
public class ProfileService {
    private final TouristeRepository touristeRepository;
    private final PaymentMethodService paymentMethodService;

    public ProfileService(TouristeRepository touristeRepository, PaymentMethodService paymentMethodService) {
        this.touristeRepository = touristeRepository;
        this.paymentMethodService = paymentMethodService;
    }

    public Profile getProfileForTouriste(Long touristeId) {
        return touristeRepository.findById(touristeId).orElseThrow(() -> new RuntimeException("Touriste not found")).getProfile();
    }

    public Profile save(Profile p) {
        // Profiles are persisted via Touriste cascade in this design; allow direct save by attaching to dummy touriste not implemented.
        throw new UnsupportedOperationException("Save profiles via Touriste endpoints");
    }

    public List<Profile> listAll() {
        // For simplicity, collect from all tourists
        return touristeRepository.findAll().stream().map(t -> t.getProfile()).toList();
    }

    public Profile updatePreferencesForCurrentUser(Preferences preferences) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Updating preferences for user: " + username);
        TouristeEntity t = touristeRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Touriste not found"));
        if (t.getProfile() == null) {
            t.setProfile(Profile.builder().build());
        }
        t.getProfile().setPreferences(preferences);
        TouristeEntity saved = touristeRepository.save(t);
        return saved.getProfile();
    }

    @Transactional
    public PaymentMethod addPaymentMethodForCurrentUser(PaymentMethod pm) {
        String username = null;
        try {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ex) {
            // no authentication present
        }
        System.out.println("addPaymentMethodForCurrentUser called, auth username=" + username);
        TouristeEntity t = touristeRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Touriste not found"));
        PaymentMethod savedPm = paymentMethodService.save(pm);
        if (t.getProfile() == null) {
            t.setProfile(Profile.builder().build());
        }
        if (t.getProfile().getPaymentMethods() == null) {
            t.getProfile().setPaymentMethods(new ArrayList<>());
        }
        t.getProfile().getPaymentMethods().add(savedPm);
        touristeRepository.save(t);
        return savedPm;
    }
}
