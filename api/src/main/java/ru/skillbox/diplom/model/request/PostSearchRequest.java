package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PostSearchRequest {
    private String text;
    @JsonProperty("date_from")
    private LocalDate dateFrom;
    @JsonProperty("date_to")
    private LocalDate dateTo;
    private String author;
}
