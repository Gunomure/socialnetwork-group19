package ru.skillbox.diplom.websocket.structs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageResponse {
    @JsonProperty("recipient_id")
    private Long recipientId;
    private String message;
    @JsonProperty("message_id")
    private Long messageId;
    Long time;
}