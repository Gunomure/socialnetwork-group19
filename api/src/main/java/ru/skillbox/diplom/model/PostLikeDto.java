package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class PostLikeDto {
    private LocalDateTime time;
    @JsonProperty("person_id")
    private Long personId;
    @JsonProperty("post_id")
    private Long postId;
}
