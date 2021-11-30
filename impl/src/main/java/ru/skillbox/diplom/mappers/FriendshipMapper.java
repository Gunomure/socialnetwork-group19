package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.Friendship;
import ru.skillbox.diplom.model.FriendshipResponseDto;
import ru.skillbox.diplom.model.FriendshipStatus;
import ru.skillbox.diplom.model.Person;

import java.util.List;

@Mapper(uses = {Converters.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FriendshipMapper {

    @Mapping(target = "id", source = "friendship.dstPerson.id")
    @Mapping(target = "firstName", source = "friendship.dstPerson.firstName")
    @Mapping(target = "lastName", source = "friendship.dstPerson.lastName")
    @Mapping(target = "birthDate", source = "friendship.dstPerson.birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "email", source = "friendship.dstPerson.email")
    @Mapping(target = "registrationDate", source = "friendship.dstPerson.registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "phone", source = "friendship.dstPerson.phone")
    @Mapping(target = "photo", source = "friendship.dstPerson.photo")
    @Mapping(target = "description", source = "friendship.dstPerson.description")
    @Mapping(target = "city", source = "friendship.dstPerson.city")
    @Mapping(target = "country", source = "friendship.dstPerson.country")
    @Mapping(target = "permission", source = "friendship.dstPerson.permission")
    @Mapping(target = "lastOnlineTime", source = "friendship.dstPerson.lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "blocked", source = "friendship.dstPerson.isBlocked")
    @Mapping(target = "friendshipStatus", source = "friendship.statusId.code")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "posts", ignore = true)
    FriendshipResponseDto dstToFriendship(Friendship friendship);

    List<FriendshipResponseDto> dstToFriendship(List<Friendship> friendships);

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
    @Mapping(target = "friendshipStatus", source = "status.code")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "posts", ignore = true)
    FriendshipResponseDto personToFriendship(Person person, FriendshipStatus status);
}
