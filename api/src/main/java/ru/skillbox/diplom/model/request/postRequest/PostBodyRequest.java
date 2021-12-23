package ru.skillbox.diplom.model.request.postRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PostBodyRequest {
    private String title;
    @JsonProperty("post_text")
    private String postText;
    private String[] tags;
}
