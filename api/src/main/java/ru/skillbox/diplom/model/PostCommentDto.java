package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostCommentDto {

    private Long id;

    private Long time;

    @JsonProperty(value = "comment_text")
    private String commentText;

    @JsonProperty(value = "is_blocked")
    private boolean isBlocked;

    @JsonProperty(value = "parent_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long parentId;

    @JsonProperty(value = "post_id")
    private Long postId;

    @JsonProperty(value = "author_id")
    private Long authorId;
}