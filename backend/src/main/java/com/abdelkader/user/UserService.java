package com.abdelkader.user;

import com.abdelkader.country.CountryRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository    userRepository;
    private final CountryRepository countryRepository;

    public UserService(UserRepository userRepository, CountryRepository countryRepository) {
        this.userRepository    = userRepository;
        this.countryRepository = countryRepository;
    }

    public boolean auth(UserAuthDTO user) {
        return userRepository.existsByEmailAndPassword(user.email(), user.password());
    }

    public User createUser(UserCreationDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.username());
        user.setPassword(userDTO.password());
        user.setEmail(userDTO.email());
        user.setCountry(countryRepository.getReferenceById(userDTO.country_id()));

        userRepository.save(user);
        return user;
    }
}
