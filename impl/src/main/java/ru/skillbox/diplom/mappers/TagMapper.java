package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skillbox.diplom.model.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TagMapper {

//    TagMapper INSTANCE = Mappers.getMapper(TagMapper.class);

    TagDto toPostDto(Tag tag);

    List<TagDto> toPostDto(List<Tag> tags);

}
