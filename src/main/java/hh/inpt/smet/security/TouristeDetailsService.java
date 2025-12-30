package hh.inpt.smet.security;

import hh.inpt.smet.tourist.persistence.TouristeRepository;
import hh.inpt.smet.tourist.model.TouristeEntity;
import java.util.Collections;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TouristeDetailsService implements UserDetailsService {

    private final TouristeRepository repo;

    public TouristeDetailsService(TouristeRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TouristeEntity t = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(t.getUsername()).password(t.getPassword()).authorities(Collections.emptyList()).build();
    }
}
