package hh.inpt.smet.tourist.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hh.inpt.smet.tourist.model.TouristeEntity;
import java.util.Optional;

@Repository
public interface TouristeRepository extends JpaRepository<TouristeEntity, Long> {
    Optional<TouristeEntity> findByUsername(String username);
}
