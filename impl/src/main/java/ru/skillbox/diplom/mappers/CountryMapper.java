package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import ru.skillbox.diplom.model.Country;
import ru.skillbox.diplom.model.CountryDto;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDto toCountryDTO(Country country);

    Country toCountryEntity(CountryDto countryDTO);


}
