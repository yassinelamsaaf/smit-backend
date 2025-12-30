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
public class HotelReservationRequestDTO {
    private Long hotelId;
    private Date dateDebut;
    private Date dateFin;
    private Long paymentMethodId;
}
