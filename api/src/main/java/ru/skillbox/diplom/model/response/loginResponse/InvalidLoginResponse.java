package ru.skillbox.diplom.model.response.loginResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class InvalidLoginResponse {

    private final String error = "invalid password or email";

    @JsonProperty(value = "error_description")
    private final String errorDescription = "invalid password or email. Be sure that the input data is correct and make sure that the capslock is not clamped";

}
