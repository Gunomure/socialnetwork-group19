package ru.skillbox.diplom.mappers;

import org.mapstruct.*;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostComment;
import ru.skillbox.diplom.model.PostCommentDto;
import ru.skillbox.diplom.util.MapperUtils;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring",
//        uses = {MapperUtils.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface PostCommentMapper {

//    PostCommentMapper INSTANCE = Mappers.getMapper(PostCommentMapper.class);

    @Mappings({
            @Mapping(target = "time", qualifiedByName =  "zonedDateTimeToLong"), //{ "Utils", "zonedDateTimeToLong" }),
            @Mapping(target = "blocked", ignore = true)
    })

    PostCommentDto toDtoPostComment(PostComment postComment);

    List<PostCommentDto> toDtoPostComment(Collection<PostComment> comments);

    @Named("zonedDateTimeToLong")
    static Long map(ZonedDateTime time){
        return TimeUtil.zonedDateTimeToLong(time);
    }

            static Long map(Person person){
        return person.getId();
    }

    static Long map(Post post){
        return post.getId();
    }

    static Long map(PostComment comment){
        return comment.getId();
    }

}
