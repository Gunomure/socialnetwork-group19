package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Country;
import ru.skillbox.diplom.model.CountryDTO;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    static CountryMapper getInstance(){
        return Mappers.getMapper(CountryMapper.class);
    }

    CountryDTO toCountryDTO(Country country);

    Country toCountryEntity(CountryDTO countryDTO);


}
