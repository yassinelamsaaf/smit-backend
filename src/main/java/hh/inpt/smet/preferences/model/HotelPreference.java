package hh.inpt.smet.preferences.model;

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
@Table(name = "HOTEL_PREFERENCES")
@EqualsAndHashCode(callSuper = true)
public class HotelPreference extends Preferences {

    private int nbEtoilesMin;
    private String preferredCity;
}
