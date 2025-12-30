package hh.inpt.smet.hotel.service;

import hh.inpt.smet.hotel.dto.HotelReservationRequestDTO;
import hh.inpt.smet.hotel.dto.HotelReservationResponseDTO;
import hh.inpt.smet.hotel.model.ReservationHotel;
import hh.inpt.smet.hotel.persistence.ReservationHotelRepository;
import hh.inpt.smet.billing.model.Facturation;
import hh.inpt.smet.billing.persistence.FacturationRepository;
import hh.inpt.smet.tourist.persistence.TouristeRepository;
import hh.inpt.smet.tourist.model.TouristeEntity;
import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.preferences.model.HotelPreference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
 

@Service
public class HotelReservationService {

    private final TouristeRepository touristeRepository;
    private final ReservationHotelRepository reservationRepo;
    private final FacturationRepository factureRepo;
    private final HotelService hotelService;

    public HotelReservationService(TouristeRepository touristeRepository, ReservationHotelRepository reservationRepo, FacturationRepository factureRepo, HotelService hotelService) {
        this.touristeRepository = touristeRepository;
        this.reservationRepo = reservationRepo;
        this.factureRepo = factureRepo;
        this.hotelService = hotelService;
    }

    @Transactional
    public HotelReservationResponseDTO createReservation(HotelReservationRequestDTO req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TouristeEntity touriste = touristeRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        // Require at least one payment method
        if (touriste.getProfile() == null || touriste.getProfile().getPaymentMethods() == null || touriste.getProfile().getPaymentMethods().isEmpty()) {
            throw new RuntimeException("No payment method registered");
        }

        // check hotels against preference and find hotel by id
        HotelPreference pref = null;
        if (touriste.getProfile() != null && touriste.getProfile().getPreferences() instanceof HotelPreference) {
            pref = (HotelPreference) touriste.getProfile().getPreferences();
        }
        List< hh.inpt.smet.hotel.dto.HotelDTO> available = hotelService.listFilteredByPreference(pref);
        hh.inpt.smet.hotel.dto.HotelDTO hotel = hotelService.findById(req.getHotelId());
        if (hotel == null || available.stream().noneMatch(h -> h.getId().equals(hotel.getId()))) {
            throw new RuntimeException("Hotel not available according to preferences");
        }

        double prix = hotel.getPrixParNuit();

        long diffMs = Math.max(1, (req.getDateFin().getTime() - req.getDateDebut().getTime())/(1000*60*60*24));
        double montantTotal = prix * diffMs;

        // validate payment method belongs to user
        PaymentMethod payment = null;
        if (req.getPaymentMethodId() != null && touriste.getProfile() != null && touriste.getProfile().getPaymentMethods() != null) {
            for (PaymentMethod pm : touriste.getProfile().getPaymentMethods()) {
                if (pm.getId() != null && pm.getId().equals(req.getPaymentMethodId())) {
                    payment = pm;
                    break;
                }
            }
        }
        if (payment == null) {
            throw new RuntimeException("Payment method not found for user");
        }

        ReservationHotel reservation = ReservationHotel.builder()
                .hotelName(hotel.getHotelName())
                .city(hotel.getCity())
                .nbEtoiles(hotel.getNbEtoiles())
                .prixParNuit(prix)
                .dateDebut(req.getDateDebut())
                .dateFin(req.getDateFin())
                .touristeId(touriste.getId())
                .build();

        ReservationHotel saved = reservationRepo.save(reservation);

        Facturation facture = Facturation.builder()
                .idFacture("F-" + System.currentTimeMillis())
                .montantTotal(montantTotal)
                .dateEmission(new Date())
                .statutPaiement("PENDING")
                .touristeId(touriste.getId())
                .reservationHotel(saved)
            .paymentMethod(payment)
                .build();

        Facturation savedFact = factureRepo.save(facture);

        return HotelReservationResponseDTO.builder()
                .reservationId(saved.getId())
                .factureId(savedFact.getId())
                .montantTotal(montantTotal)
                .statutPaiement(savedFact.getStatutPaiement())
                .dateDebut(saved.getDateDebut())
                .dateFin(saved.getDateFin())
            .hotel(hotel)
                .build();
    }

    public java.util.List<hh.inpt.smet.hotel.dto.HotelDTO> listAvailableHotelsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TouristeEntity touriste = touristeRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        HotelPreference pref = null;
        if (touriste.getProfile() != null && touriste.getProfile().getPreferences() instanceof HotelPreference) {
            pref = (HotelPreference) touriste.getProfile().getPreferences();
        }
        return hotelService.listFilteredByPreference(pref);
    }

    @Transactional
    public void payFacture(Long factureId) {
        Facturation f = factureRepo.findById(factureId).orElseThrow(() -> new RuntimeException("Facture not found"));
        f.setStatutPaiement("PAID");
        factureRepo.save(f);
    }
}
