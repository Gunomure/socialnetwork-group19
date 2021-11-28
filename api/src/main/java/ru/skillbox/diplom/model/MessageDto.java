package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageDto {
    private Long id;
    private Long time;
    @JsonProperty(value ="author_id")
    private Long authorId;
    @JsonProperty(value ="recipient_id")
    private Long recipientId;
    @JsonProperty(value ="message_text")
    private String messageText;
    private String status;
}







