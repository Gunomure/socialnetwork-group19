package ru.skillbox.diplom.model.response.dialogs;

import lombok.Data;
import ru.skillbox.diplom.model.MessageDto;

import java.util.List;

@Data
public class DialogMessagesResponse {
    String error;
    Long timestamp;
    int total;
    int offset;
    int perPage;
    List<MessageDto> data;
}

