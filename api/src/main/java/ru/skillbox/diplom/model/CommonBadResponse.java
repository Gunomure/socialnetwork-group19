package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommonBadResponse {
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;

    public CommonBadResponse(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }
}
