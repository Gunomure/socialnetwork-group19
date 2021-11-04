package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Country;
import ru.skillbox.diplom.model.CountryDto;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    static CountryMapper getInstance(){
        return Mappers.getMapper(CountryMapper.class);
    }

    CountryDto toCountryDTO(Country country);

    Country toCountryEntity(CountryDto countryDTO);


}
