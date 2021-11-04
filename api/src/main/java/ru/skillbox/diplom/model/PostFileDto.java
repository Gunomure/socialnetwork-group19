package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PostFileDto {
    @JsonProperty("post_id")
    private Long postId;
    private String name;
    private String path;
}
