package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Tag;
import ru.skillbox.diplom.model.TagDto;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.TagListResponse;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = Converters.class,
        imports = {ZonedDateTime.class, MessageResponse.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TagMapper {

    TagDto convertToTagDto(Tag tag);

    List<TagDto> convertToTagDtoList(List<Tag> tags);

    @Mapping(source = "tags", target = "total",  qualifiedByName = "convertCollectionToSize")
    @Mapping(source = "tags", target = "data")
    @Mapping(target = "perPage", source = "perPage")
    @Mapping(target = "offset", source = "offset")
    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "error", ignore = true)
    TagListResponse convertToTagListResponse(Integer offset, Integer perPage, List<Tag> tags);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "tagEntity")
    @Mapping(target = "error", ignore = true)
    CommonResponse<TagDto> convertToCommonResponse(Tag tagEntity);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "response")
    @Mapping(target = "error", ignore = true)
    CommonResponse<MessageResponse> convertToCommonResponse(MessageResponse response);
}
