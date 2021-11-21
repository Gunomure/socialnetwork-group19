package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.response.postResponse.CommentListResponse;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = Converters.class,
        imports = ZonedDateTime.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PostCommentMapper {

    @Mapping(target = "time", qualifiedByName =  "convertDateToLong")
    @Mapping(target = "blocked", source = "isBlocked")
    @Mapping(target = "parentId", source = "parent", qualifiedByName = "convertCommentToId")
    @Mapping(target = "postId", source = "post", qualifiedByName = "convertPostToId")
    @Mapping(target = "authorId", source = "author", qualifiedByName = "convertPersonToId")
    PostCommentDto convertToPostCommentDto(PostComment postComment);

    List<PostCommentDto> convertToPostCommentListDto(Collection<PostComment> comments);

    @Mapping(target = "time", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "isBlocked", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentText", source = "commentText")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "parent", source = "parent")
    PostComment convertToPostCommentEntity(Post post, Person author, PostComment parent, String commentText);


    @Mapping(source = "comments", target = "total",  qualifiedByName = "convertCollectionToSize")
    @Mapping(source = "comments", target = "data")
    @Mapping(target = "perPage", source = "itemPerPage")
    @Mapping(target = "offset", source = "offset")
    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "error", ignore = true)
    CommentListResponse convertToPostCommentListResponse(Integer offset,
                                                         Integer itemPerPage,
                                                         List<PostComment> comments);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "comment")
    @Mapping(target = "error", ignore = true)
    CommonResponse<PostCommentDto> convertToCommonResponse(PostComment comment);

    @Mapping(target = "timestamp", expression = "java(ZonedDateTime.now().toEpochSecond())")
    @Mapping(target = "data", source = "idResponse")
    @Mapping(target = "error", ignore = true)
    CommonResponse<IdResponse> convertToCommonResponse(IdResponse idResponse);

}
