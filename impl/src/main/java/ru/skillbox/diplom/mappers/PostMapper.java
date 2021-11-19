package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.*;

import java.util.List;

@Mapper(uses = {Converters.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {


    @Mapping(target = "likes", qualifiedByName = "convertLikesListToLikesSize")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "author", source = "authorId")
    PostDto convertToDto(Post post);

    @Mapping(target = "authorId", qualifiedByName = "convertPersonToId")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "parentId", qualifiedByName = "convertCommentToParentId")
    @Mapping(source = "post", target = "postId", qualifiedByName = "convertPostToId")
    PostCommentDto convertToDtoPostComment(PostComment postComment);

    List<PostDto> convertToListPostDto(List<Post> posts);

    @Mapping(target = "birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "token", ignore = true)
    PersonDto toPersonDTO(Person person);

}
