package hh.inpt.smet.hotel.service;

import hh.inpt.smet.hotel.dto.HotelDTO;
import hh.inpt.smet.preferences.model.HotelPreference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final List<HotelDTO> hotels = new ArrayList<>();

    public HotelService() {
        // mock hotels (with ids)
        hotels.add(HotelDTO.builder().id(101L).hotelName("Grand Palace").city("Rabat").nbEtoiles(5).prixParNuit(200.0).build());
        hotels.add(HotelDTO.builder().id(102L).hotelName("Hotel Plaza").city("Rabat").nbEtoiles(4).prixParNuit(80.0).build());
        hotels.add(HotelDTO.builder().id(103L).hotelName("Ocean View").city("Rabat").nbEtoiles(3).prixParNuit(60.0).build());
        hotels.add(HotelDTO.builder().id(104L).hotelName("Budget Inn").city("Lyon").nbEtoiles(2).prixParNuit(45.0).build());
        hotels.add(HotelDTO.builder().id(105L).hotelName("Sea View").city("Nice").nbEtoiles(4).prixParNuit(150.0).build());
    }

    public List<HotelDTO> listHotels() {
        return new ArrayList<>(hotels);
    }

    public List<HotelDTO> listFilteredByPreference(HotelPreference pref) {
        if (pref == null) return listHotels();
        String preferredCity = pref.getPreferredCity();
        int minStars = pref.getNbEtoilesMin();
        return hotels.stream()
            .filter(h -> h.getNbEtoiles() >= minStars)
            .filter(h -> preferredCity == null || preferredCity.isBlank() || h.getCity().equalsIgnoreCase(preferredCity))
            .collect(Collectors.toList());
    }

    public HotelDTO findById(Long id) {
        return hotels.stream().filter(h -> h.getId().equals(id)).findFirst().orElse(null);
    }
}
