package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.util.MapperUtils;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {PostCommentMapper.class, PersonMapper.class, //TagMapper.class,
        MapperUtils.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PostMapper {

//    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mappings({
            @Mapping(target = "likes", qualifiedByName = { "Utils", "listToIntSize" }),
            @Mapping(target = "author", source = "authorId"),
            @Mapping(target = "time", qualifiedByName = { "Utils", "zonedDateTimeToLong" })
    })

    PostDto toPostDto(Post post);

    List<PostDto> toPostDto(List<Post> post);

}
