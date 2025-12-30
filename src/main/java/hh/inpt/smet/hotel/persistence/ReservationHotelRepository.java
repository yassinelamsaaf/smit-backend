package hh.inpt.smet.hotel.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import hh.inpt.smet.hotel.model.ReservationHotel;

@Repository
public interface ReservationHotelRepository extends JpaRepository<ReservationHotel, Long> {

}
