package ru.skillbox.diplom.model.response.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OneLastMessagesResponseDto {
    String error;
    Long timestamp;
    int total;
    int offset;
    int perPage;
    @JsonProperty("current_user_id")
    Long currentUserId;
    List<OneLastMessageResponseDtoBody> data;
}

