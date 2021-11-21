package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.CommentReport;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PostComment;
import ru.skillbox.diplom.model.response.MessageResponse;

import java.time.ZonedDateTime;

@Mapper(componentModel = "spring",
        uses = Converters.class,
        imports = ZonedDateTime.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ReportMapper {

    @Mapping(target = "time", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "comment", source = "comment")
    CommentReport convertToCommentReportEntity(PostComment comment);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "messageResponse")
    @Mapping(target = "error", ignore = true)
    CommonResponse<MessageResponse> convertToCommonResponse(MessageResponse messageResponse);
}
