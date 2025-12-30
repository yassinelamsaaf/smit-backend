package hh.inpt.smet.tourist.dto;

import hh.inpt.smet.profile.model.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristeDTO {
    private Long id;
    private String username;
    private String email;
    private String nom;
    private String prenom;
    private String telephone;
    private Profile profile;
}
