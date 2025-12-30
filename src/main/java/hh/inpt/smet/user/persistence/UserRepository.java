package hh.inpt.smet.user.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hh.inpt.smet.user.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>  {

}
