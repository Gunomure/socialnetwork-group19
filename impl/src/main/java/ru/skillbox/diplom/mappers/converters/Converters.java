package ru.skillbox.diplom.mappers.converters;

import org.mapstruct.Named;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostComment;
import ru.skillbox.diplom.model.PostLike;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class Converters {

    @Named("convertDateToLong")
    public static Long convertDateToLong(ZonedDateTime time) {
        return time.toEpochSecond();
    }

    @Named("convertLongToDate")
    public static ZonedDateTime convertLongToDate(Long time) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
    }

    @Named("convertPersonToId")
    public static Long convertPersonToId(Person person) {
        return person.getId();
    }

    @Named("convertLikesListToLikesSize")
    public static Long convertLikesListToLikesSize(List<PostLike> likes) {
        return (long) likes.size();
    }

    @Named("convertCommentToParentId")
    public static Long convertCommentToParentId(PostComment comment) {
        return comment.getParentId().getId();
    }

    @Named("convertPostToId")
    public static Long convertPostToId(Post post) {
        return post.getId();
    }

    @Named("convertToTimestampNow")
    public static Long convertToTimestampNow() {
        return TimeUtil.getCurrentTimestampUtc();
    }

}
