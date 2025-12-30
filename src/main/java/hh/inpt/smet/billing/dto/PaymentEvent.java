package hh.inpt.smet.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    private Long factureId;
    private Long paymentMethodId;
    private String paymentType; // "BANK" or "TELECOM"
    private Double amount;
    private Long touristeId;
    private Integer retryCount;

}
