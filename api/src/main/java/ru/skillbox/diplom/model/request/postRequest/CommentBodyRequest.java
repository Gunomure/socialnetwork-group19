package ru.skillbox.diplom.model.request.postRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentBodyRequest {

    @JsonProperty("parent_id")
    private Long parentId;

    @JsonProperty("comment_text")
    private String commentText;
}
