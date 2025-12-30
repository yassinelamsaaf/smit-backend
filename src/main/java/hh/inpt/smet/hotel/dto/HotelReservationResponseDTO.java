package hh.inpt.smet.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelReservationResponseDTO {
    private Long reservationId;
    private Long factureId;
    private double montantTotal;
    private String statutPaiement;
    private Date dateDebut;
    private Date dateFin;
    private HotelDTO hotel;
}
