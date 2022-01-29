package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.NotificationTypes;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(uses = {Converters.class},
        imports = ZonedDateTime.class,
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface NotificationMapper {

    @Mapping(target = "sentTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "typeId", source = "typeId")
    NotificationDto convertToDto(Notification notification);

    List<NotificationDto> convertToListDto(List<Notification> notifications);

    NotificationTypeDto toTypeDto(NotificationType type);
}
