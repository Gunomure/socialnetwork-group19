package ru.skillbox.diplom.websocket.structs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPair {
    Long authorId;
    Long recipientId;
}
