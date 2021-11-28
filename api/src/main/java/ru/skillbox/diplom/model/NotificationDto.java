package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class NotificationDto {
    @JsonProperty(value ="type_id")
    private Long typeId;
    @JsonProperty(value ="sent_time")
    private ZonedDateTime sentTime;
    @JsonProperty(value ="person_id")
    private Long personId;
    @JsonProperty(value ="entity_id")
    private Long entityId;
    private String contact;
}

