package ru.skillbox.diplom.websocket.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageBody {
    @JsonProperty("recipient_id")
    private Long recipientId;//TODO check in front
    private String message;
    //private String jwt;
}
