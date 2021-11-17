package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;

import java.util.Collection;
import java.util.List;

@Mapper(uses = {Converters.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {

    @Mapping(target = "birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "token", ignore = true)
    PersonDto toPersonDTO(Person person);

    @Mapping(target = "birthDate", qualifiedByName = "convertLongToDate")
    @Mapping(target = "registrationDate", qualifiedByName = "convertLongToDate")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "convertLongToDate")
    Person toPersonEntity(PersonDto personDTO);

    List<PersonDto> toListPersonDTO(Collection<Person> persons);
}
