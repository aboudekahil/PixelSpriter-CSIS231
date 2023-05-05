package com.abdelkader.country;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {
    private final CountryRepository countryRepository;

    public CountryController(CountryRepository countryRepository) {this.countryRepository = countryRepository;}

    @GetMapping
    public List<Country> getCountries(){
        return countryRepository.findAll();
    }
}
