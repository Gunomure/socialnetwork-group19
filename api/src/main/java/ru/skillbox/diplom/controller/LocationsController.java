package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.CityDto;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.CountryDto;

import java.util.List;

@CrossOrigin
@RequestMapping("/api/v1/geo")
@PreAuthorize("hasAuthority('developers:read')")
public interface LocationsController{

    @GetMapping("/countries")
    CommonResponse<List<CountryDto>> getCountries();

    @GetMapping("/cities/{countryId}")
    CommonResponse<List<CityDto>> getCitiesById(@PathVariable Long countryId);

    @GetMapping("/cities")
    void getCities();

}
