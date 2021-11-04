package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.City;
import ru.skillbox.diplom.model.CityDto;

@Mapper(componentModel = "spring")
public interface CityMapper {

    static CityMapper getInstance(){
        return Mappers.getMapper(CityMapper.class);
    }

    CityDto toCityDTO(City city);

    City toCityEntity(CityDto cityDTO);
}
