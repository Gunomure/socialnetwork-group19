package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @JsonProperty("new_password")
    private String newPassword;
}
