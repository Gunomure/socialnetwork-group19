package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmailChangeRequest {
    @JsonProperty("new_email")
    private String newEmail;
}
