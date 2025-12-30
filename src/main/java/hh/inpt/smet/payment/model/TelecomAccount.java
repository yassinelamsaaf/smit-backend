package hh.inpt.smet.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "TELECOM_ACCOUNTS")
@EqualsAndHashCode(callSuper = true)
public class TelecomAccount extends PaymentMethod {

    private String numeroMobile;
    private String operateur;
}
