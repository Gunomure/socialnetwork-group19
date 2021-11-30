package ru.skillbox.diplom.mappers.converters;

import org.mapstruct.Named;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
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

    @Named("convertCommentLikesListToLikesSize")
    public static Long convertCommentLikesListToLikesSize(List<CommentLike> likes) {
        return (long) likes.size();
    }

    @Named("convertCommentToId")
    public static Long convertCommentToId(PostComment comment) {
        return comment == null ? null : comment.getId();
    }

    @Named("convertPostToId")
    public static Long convertPostToId(Post post) {
        return post.getId();
    }

    @Named("convertToTimestampNow")
    public static Long convertToTimestampNow() {
        return TimeUtil.getCurrentTimestampUtc();
    }

    @Named("convertCollectionToSize")
    public static Integer convertCollectionToSize(Collection<?> collection){
        return collection.size();
    }
}
