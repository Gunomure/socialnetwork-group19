package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.*;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Mapper(uses = {Converters.class},
        imports = ZonedDateTime.class,
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "likes", qualifiedByName = "convertLikesListToLikesSize")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "myLike", ignore = true)
    PostDto convertToDto(Post post);

    @Mapping(target = "authorId", source = "author")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "parentId", source = "parent", qualifiedByName = "convertCommentToId")
    @Mapping(target = "postId", source = "post", qualifiedByName = "convertPostToId")
    @Mapping(target = "likes", qualifiedByName = "convertCommentLikesListToLikesSize")
    PostCommentDto convertToDtoPostComment(PostComment postComment);

    List<PostDto> convertToListPostDto(Collection<Post> posts);

    @Mapping(target = "birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "posts", ignore = true)
    PersonDto toPersonDTO(Person person);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "post")
    @Mapping(target = "error", ignore = true)
    CommonResponse<PostDto> convertToCommonResponse(Post post);

    @Mapping(target = "tag", source = "postToTag.tagId.tag")
    @Mapping(target = "id", source = "postToTag.tagId.id")
    TagDto convertToTagDto(PostToTag postToTag);

    List<TagDto> convertToTagDtoList(List<PostToTag> postToTags);
}
