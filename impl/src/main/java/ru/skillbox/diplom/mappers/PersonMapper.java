package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDTO;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    static PersonMapper getInstance(){
        return Mappers.getMapper(PersonMapper.class);
    }

    PersonDTO toPersonDTO(Person person);

    Person toPersonEntity(PersonDTO personDTO);

    Date map(Long value);
}
