package hh.inpt.smet.billing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hh.inpt.smet.billing.model.Facturation;

@Repository
public interface FacturationRepository extends JpaRepository<Facturation, Long> {

}
