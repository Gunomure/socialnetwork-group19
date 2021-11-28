package ru.skillbox.diplom.websocket.structs;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class MessageClient {
    private Long userId;
    private SocketIOClient client;
}
