package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.City;
import ru.skillbox.diplom.model.CityDTO;

@Mapper(componentModel = "spring")
public interface CityMapper {

    static CityMapper getInstance(){
        return Mappers.getMapper(CityMapper.class);
    }

    CityDTO toCityDTO(City city);

    City toCityEntity(CityDTO cityDTO);
}
