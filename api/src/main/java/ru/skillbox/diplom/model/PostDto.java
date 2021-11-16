package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDto {

    private Long id;

    private Long time;

    private PersonDto author;

    private String title;

    @JsonProperty("post_text")
    private String postText;

    @JsonProperty("is_blocked")
    private Boolean isBlocked;

    private int likes;

    private List<PostCommentDto> comments;

    private List<TagDto> tags;
}