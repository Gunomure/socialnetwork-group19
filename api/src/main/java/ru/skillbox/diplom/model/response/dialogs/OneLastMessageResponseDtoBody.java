package ru.skillbox.diplom.model.response.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OneLastMessageResponseDtoBody {
    Long  id;
    @JsonProperty("unread_count")
    Long unreadCount;
    @JsonProperty("last_message")
    LastMessageDto lastMessage;
}
