package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.util.MapperUtils;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring",
//        uses = {MapperUtils.class, CityMapper.class, CountryMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PersonMapper {

//    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);

    @Mappings({
            @Mapping(target = "birthDate", qualifiedByName = "dateToLong"),//{ "Utils", "dateToLong" }),
            @Mapping(target = "registrationDate", qualifiedByName = "zonedDateTimeToLong"),//{ "Utils", "zonedDateTimeToLong" }),
            @Mapping(target = "lastOnlineTime", qualifiedByName = "zonedDateTimeToLong"),//{ "Utils", "zonedDateTimeToLong" }),
            @Mapping(target = "token", ignore = true)
    })
    PersonDto toPersonDTO(Person person);

    List<PersonDto> toPersonDTO(Collection<Person> persons);

    Person toPersonEntity(PersonDto personDto);

    Date map(Long value);

    @Named("dateToLong")
    static Long map(Date date){
        return date.getTime();
    }
    @Named("zonedDateTimeToLong")
    static Long map(ZonedDateTime time){
        return TimeUtil.zonedDateTimeToLong(time);
    }

}
