package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostComment;
import ru.skillbox.diplom.model.PostCommentDto;
import ru.skillbox.diplom.model.PostDto;

import java.util.List;

@Mapper(uses = {Converters.class, PersonMapper.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PostMapper {

    @Mapping(target = "likes", qualifiedByName = "convertLikesListToLikesSize")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    PostDto convertToDto(Post post);

    @Mapping(target = "authorId", qualifiedByName = "convertPersonToId")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "parentId", qualifiedByName = "convertCommentToParentId")
    @Mapping(source = "post", target = "postId", qualifiedByName = "convertPostToId")
    PostCommentDto convertToDtoPostComment(PostComment postComment);

    List<PostDto> convertToListPostDto(List<Post> posts);

}
