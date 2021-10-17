package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.NewPersonDto;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    static PersonMapper getInstance(){
        return Mappers.getMapper(PersonMapper.class);
    }

    NewPersonDto toDto(Person person);

    Person toEntity(NewPersonDto personDto);
}
