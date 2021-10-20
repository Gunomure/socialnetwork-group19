package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;

public class NotificationDto {
    @JsonProperty("type_id")
    private Long typeId;
    @JsonProperty("sent_time")
    private ZonedDateTime sentTime;
    @JsonProperty("person_id")
    private Long personId;
    @JsonProperty("entity_id")
    private Long entityId;
    private String contact;
}

