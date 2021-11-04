package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostToTagDto {
    @JsonProperty("post_id")
    private Long postId;
    @JsonProperty("tag_id")
    private Long tagId;
}
