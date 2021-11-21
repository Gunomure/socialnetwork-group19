package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.response.FeedsResponse;
import ru.skillbox.diplom.model.response.postResponse.CommentListResponse;
import ru.skillbox.diplom.util.TimeUtil;

import java.util.List;

@Mapper(uses = {Converters.class},
        imports = TimeUtil.class,
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ResponseMapper {

    @Mapping(target = "offset", source = "offset")
    @Mapping(target = "perPage", source = "perPage")
    public abstract FeedsResponse<List<PostDto>> convertToFeedsResponse(Integer offset, Integer perPage);

    public abstract List<PostDto> toListPostDto(List<Post> posts);

    @Mapping(target = "likes", qualifiedByName = "convertLikesListToLikesSize")
    @Mapping(target = "time", qualifiedByName = "convertDateToLong")
    @Mapping(target = "author", source = "authorId")
    public abstract PostDto convertToDto(Post post);

    @Mapping(target = "birthDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "registrationDate", qualifiedByName = "convertDateToLong")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "convertDateToLong")
    @Mapping(target = "token", ignore = true)
    public abstract PersonDto toPersonDTO(Person person);

    @Mapping(target = "time", qualifiedByName =  "convertDateToLong")
    @Mapping(target = "blocked", source = "isBlocked")
    @Mapping(target = "parentId", source = "parent", qualifiedByName = "convertCommentToId")
    @Mapping(target = "postId", source = "post", qualifiedByName = "convertPostToId")
    @Mapping(target = "authorId", source = "author", qualifiedByName = "convertPersonToId")
    public abstract PostCommentDto convertToDtoPostComment(PostComment postComment);

    public void updateToFeedsResponse(List<Post> list, FeedsResponse<List<PostDto>> response) {
        response.setTotal(list.size());
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        if (list.size() == 0) {
            response.setError("По указанному запросу не найдено новостей");
            return;
        }
        response.setData(toListPostDto(list));
    }
}
