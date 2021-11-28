package ru.skillbox.diplom.model.request.postRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String text;
    @JsonProperty("date_from")
    private ZonedDateTime dateFrom;
    @JsonProperty("date_to")
    private ZonedDateTime dateTo;
    private String author;
}
