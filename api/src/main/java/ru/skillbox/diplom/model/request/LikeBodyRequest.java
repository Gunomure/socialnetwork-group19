package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeBodyRequest {
    @JsonProperty("item_id")
    private Long itemId;
    @JsonProperty("post_id")
    private Long postId;
    private String type;

}
