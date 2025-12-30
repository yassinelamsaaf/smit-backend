package hh.inpt.smet.tourist.model;

import hh.inpt.smet.user.model.UserEntity;
import hh.inpt.smet.profile.model.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
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
@Table(name = "TOURISTES")
@EqualsAndHashCode(callSuper = true)
public class TouristeEntity extends UserEntity {

    private String nom;
    private String prenom;
    private String telephone;

    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

}
