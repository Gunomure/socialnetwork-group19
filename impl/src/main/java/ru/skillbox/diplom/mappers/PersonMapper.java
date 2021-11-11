package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static ru.skillbox.diplom.util.TimeUtil.zonedDateTimeToLong;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    static PersonMapper getInstance() {
        return Mappers.getMapper(PersonMapper.class);
    }

    PersonDto toPersonDTO(Person person);

    Person toPersonEntity(PersonDto personDTO);

    List<PersonDto> toPersonDTO(Collection<Person> persons);

    Date map(Long value);

    static Long map(Date date) {
        return date == null ? null : date.getTime();
    }

    static Long map(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTimeToLong(zonedDateTime);
    }
}
