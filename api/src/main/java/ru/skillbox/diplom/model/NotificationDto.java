package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
public class NotificationDto {

    private Long id;
    @JsonProperty(value = "type_id")
    private Long typeId;
    @JsonProperty(value ="notification_type")
    private String type;
    @JsonProperty(value ="sent_time")
    private Long sentTime;
    @JsonProperty(value ="person_id")
    private Long personId;
    @JsonProperty(value ="entity_id")
    private Long entityId;
    private String contact;
    private Boolean wasSend;

    private String content;
    @JsonProperty(value ="user_name")
    private String userName;
    private String photo;
}

