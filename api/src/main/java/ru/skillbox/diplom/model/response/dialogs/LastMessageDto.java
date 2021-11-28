package ru.skillbox.diplom.model.response.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LastMessageDto {
    private Long id;
    private Long time;
    @JsonProperty("recipient_id")
    private Long recipientId;
    @JsonProperty("author")
    private PersonInfoShortDto author;
    @JsonProperty("message_text")
    private String messageText;
    private String status;
}







