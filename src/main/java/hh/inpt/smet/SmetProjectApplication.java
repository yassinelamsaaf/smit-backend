package hh.inpt.smet;

import hh.inpt.smet.payment.model.BankAccount;
import hh.inpt.smet.payment.model.TelecomAccount;
import hh.inpt.smet.preferences.model.HotelPreference;
import hh.inpt.smet.profile.model.Profile;
import hh.inpt.smet.tourist.model.TouristeEntity;
import hh.inpt.smet.tourist.persistence.TouristeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class SmetProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmetProjectApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedData(TouristeRepository touristeRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (touristeRepository.findByUsername("tourist1").isPresent()) return;

			HotelPreference pref = HotelPreference.builder().nbEtoilesMin(3).build();

			BankAccount bank = BankAccount.builder().estParDefaut(true).iban("IBAN123").bic("BIC123").build();
			TelecomAccount tel = TelecomAccount.builder().estParDefaut(false).numeroMobile("0600000000").operateur("Orange").build();

			Profile profile = Profile.builder().langue("fr").preferences(pref).paymentMethods(Arrays.asList(bank, tel)).build();

			TouristeEntity t = TouristeEntity.builder()
					.username("tourist1")
					.email("tourist1@example.com")
					.password(passwordEncoder.encode("password"))
					.nom("Doe")
					.prenom("John")
					.telephone("0600000000")
					.profile(profile)
					.build();

			touristeRepository.save(t);
		};
	}
}
