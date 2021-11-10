package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class PostCommentDto {

    private Long id;

    private Long time;

    @JsonProperty(value = "comment_text")
    private String commentText;

    @JsonProperty(value = "is_blocked")
    private boolean isBlocked;

    @JsonProperty(value = "parent_id")
    private Long parentId;

    @JsonProperty(value = "post_id")
    private Long postId;

    @JsonProperty(value = "author_id")
    private Long authorId;
}