package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.FriendshipResponseDto;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.response.FriendshipCodeDto;

@Mapper(uses = {Converters.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendshipMapper {

    @Mapping(target = "id", source = "person.id")
    @Mapping(target = "firstName", source = "person.firstName")
    @Mapping(target = "lastName", source = "person.lastName")
    @Mapping(target = "birthDate", source = "person.birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "email", source = "person.email")
    @Mapping(target = "registrationDate", source = "person.registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "phone", source = "person.phone")
    @Mapping(target = "photo", source = "person.photo")
    @Mapping(target = "description", source = "person.description")
    @Mapping(target = "city", source = "person.city")
    @Mapping(target = "country", source = "person.country")
    @Mapping(target = "permission", source = "person.permission")
    @Mapping(target = "lastOnlineTime", source = "person.lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "blocked", source = "person.isBlocked")
    @Mapping(target = "friendshipStatus", source = "code")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "posts", ignore = true)
    FriendshipResponseDto personToFriendship(Person person, FriendshipCodeDto code);
}
