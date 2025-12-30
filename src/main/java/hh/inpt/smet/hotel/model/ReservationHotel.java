package hh.inpt.smet.hotel.model;

import hh.inpt.smet.domain.Service;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RESERVATION_HOTEL")
public class ReservationHotel implements Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hotelName;
    private String city;
    private int nbEtoiles;
    private double prixParNuit;
    private Date dateDebut;
    private Date dateFin;

    private Long touristeId;

    @Override
    public String getDetails() {
        return String.format("Reservation %s in %s from %s to %s", hotelName, city, dateDebut, dateFin);
    }
}
