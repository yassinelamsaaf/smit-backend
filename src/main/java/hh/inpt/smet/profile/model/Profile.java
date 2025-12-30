package hh.inpt.smet.profile.model;

import hh.inpt.smet.payment.model.PaymentMethod;
import hh.inpt.smet.preferences.model.Preferences;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PROFILES")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String langue;

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL)
    private Preferences preferences;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL)
    private List<PaymentMethod> paymentMethods;
}
