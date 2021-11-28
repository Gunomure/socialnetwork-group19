package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.Message;
import ru.skillbox.diplom.model.MessageDto;


@Mapper(uses = {Converters.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    //@Mapping(target = "isDeleted", ignore = true)
    MessageDto toMessageDTO(Message message);

    @Mapping(target = "time", qualifiedByName = "convertLongToDate")
    Message toMessageEntity(MessageDto messageDTO);

}
