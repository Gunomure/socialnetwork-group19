package ru.skillbox.diplom.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.LocationsController;
import ru.skillbox.diplom.model.CityDto;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.CountryDto;
import ru.skillbox.diplom.service.LocationsService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class LocationController implements LocationsController {

    private final LocationsService locationsService;

    @Override
    public CommonResponse<List<CountryDto>> getCountries() {
        return locationsService.getCountries();
    }

    @Override
    public CommonResponse<List<CityDto>> getCitiesById(Long countryId) {
        return locationsService.getCities(countryId);
    }

    @Override
    @ResponseStatus(HttpStatus.OK)
    public void getCities() {

    }
}
