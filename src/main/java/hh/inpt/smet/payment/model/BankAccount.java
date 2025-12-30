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
@Table(name = "BANK_ACCOUNTS")
@EqualsAndHashCode(callSuper = true)
public class BankAccount extends PaymentMethod {

    private String iban;
    private String bic;
}
