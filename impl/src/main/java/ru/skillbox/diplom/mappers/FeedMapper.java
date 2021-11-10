package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FeedMapper {

    @Mapping(source = "authorId", target = "author", qualifiedByName = "person")
    @Mapping(target = "time", qualifiedByName = "time")
    PostDto toDto(Post post);

    List<PostDto> toDtoList(List<Post> posts);

    @Named("person")
    @Mapping(target = "birthDate", qualifiedByName = "date")
    @Mapping(target = "registrationDate", qualifiedByName = "time")
    @Mapping(target = "lastOnlineTime", qualifiedByName = "time")
    @Mapping(target = "token", ignore = true)
    PersonDto toDtoPerson(Person person);

    @Mapping(target = "time", qualifiedByName = "time")
    PostCommentDto toDtoPostComment(PostComment postComment);

    @Named("time")
    static Long map(ZonedDateTime time){
        return TimeUtil.zonedDateTimeToLong(time);
    }

    @Named("date")
    static Long map(Date date){
        return date.getTime();
    }

    static Long map(List<PostLike> likes){
        return (long) likes.size();
    }

    static Long map(PostComment comment){
        return comment.getId();
    }

    @Mapping(target = "author_id")
    static Long map(Person person){
        return person.getId();
    }

    static Long map(Post post){
        return post.getId();
    }
}
