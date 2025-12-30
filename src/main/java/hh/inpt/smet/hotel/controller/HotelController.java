package hh.inpt.smet.hotel.controller;

import hh.inpt.smet.hotel.dto.HotelDTO;
import hh.inpt.smet.hotel.dto.HotelReservationRequestDTO;
import hh.inpt.smet.hotel.dto.HotelReservationResponseDTO;
import hh.inpt.smet.hotel.service.HotelReservationService;
import hh.inpt.smet.hotel.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final HotelReservationService reservationService;

    public HotelController(HotelService hotelService, HotelReservationService reservationService) {
        this.hotelService = hotelService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<HotelDTO> listHotels() {
        return hotelService.listHotels();
    }

    @GetMapping("/available")
    public List<HotelDTO> listAvailableForUser() {
        return reservationService.listAvailableHotelsForCurrentUser();
    }

    @PostMapping("/reserve")
    public ResponseEntity<HotelReservationResponseDTO> reserve(@RequestBody HotelReservationRequestDTO req) {
        HotelReservationResponseDTO resp = reservationService.createReservation(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/pay/{factureId}")
    public ResponseEntity<?> pay(@PathVariable Long factureId) {
        reservationService.payFacture(factureId);
        return ResponseEntity.ok().build();
    }
}
