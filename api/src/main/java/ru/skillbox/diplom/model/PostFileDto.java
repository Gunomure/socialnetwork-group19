package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostFileDto {
    @JsonProperty("post_id")
    private Long postId;
    private String name;
    private String path;
}
