package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    CommonResponse<List<CityDto>> getCities(@PathVariable Long countryId);

}
