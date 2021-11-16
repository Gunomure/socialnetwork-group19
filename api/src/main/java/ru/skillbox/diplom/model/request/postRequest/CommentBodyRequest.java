package ru.skillbox.diplom.model.request.postRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentBodyRequest {
    @JsonProperty("parent_id")
    private Long id;
    @JsonProperty("comment_text")
    private String commentText;
}
