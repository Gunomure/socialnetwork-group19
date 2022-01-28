package ru.skillbox.diplom.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.mappers.CityMapper;
import ru.skillbox.diplom.mappers.CountryMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.repository.CityRepository;
import ru.skillbox.diplom.repository.CountryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationsService {

    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final CountryMapper countryMapper;
    private final CityMapper cityMapper;

    public CommonResponse<List<CountryDto>> getCountries() {
        List<Country> countryList = countryRepository.findAll();
        List<CountryDto> countries = countryList.stream().map(countryMapper::toCountryDTO).collect(Collectors.toList());
        CommonResponse<List<CountryDto>> commonResponse = new CommonResponse();
        commonResponse.setData(countries);
        return commonResponse;
    }


    public CommonResponse<List<CityDto>> getCities(Long countryId) {
        Country country = countryRepository.findById(countryId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Country id %s was not found", countryId)));
        List<City> cityList = cityRepository.getAllByCountryId(country);
        List<CityDto> cities = cityList.stream().map(cityMapper::toCityDTO).collect(Collectors.toList());
        CommonResponse<List<CityDto>> commonResponse = new CommonResponse();
        commonResponse.setData(cities);
        return commonResponse;
    }

}
