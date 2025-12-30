package hh.inpt.smet.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
    private Long id;
    private String hotelName;
    private String city;
    private int nbEtoiles;
    private double prixParNuit;
}
