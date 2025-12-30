package hh.inpt.smet.billing.model;

import hh.inpt.smet.domain.Service;
import hh.inpt.smet.payment.model.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FACTURATIONS")
public class Facturation implements Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String idFacture;
    private double montantTotal;
    private Date dateEmission;
    private String statutPaiement;

    private Long touristeId;

    @OneToOne
    private hh.inpt.smet.hotel.model.ReservationHotel reservationHotel;

    @ManyToOne
    private PaymentMethod paymentMethod;

    @Override
    public String getDetails() {
        return String.format("Facture %s: %s EUR - %s", idFacture, montantTotal, statutPaiement);
    }
}
