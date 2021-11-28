package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.MessageController;
import ru.skillbox.diplom.model.response.dialogs.OneLastMessagesResponseDto;
import ru.skillbox.diplom.model.response.dialogs.DialogMessagesResponse;
import ru.skillbox.diplom.websocket.SocketIOService;

@RestController
public class MessageControllerImpl implements MessageController {

    private final static Logger LOGGER = LogManager.getLogger(UsersControllerImpl.class);
    private final SocketIOService socketIOService;

    public MessageControllerImpl(SocketIOService socketIOService) {
        this.socketIOService = socketIOService;
    }

    @Override
    public ResponseEntity<OneLastMessagesResponseDto> getOneLastMessageFromEverybodyFull(int offset, int itemPerPage) {

        LOGGER.info("start getOneLastMessageFromEverybody:  offset={}, itemPerPage={}",
                offset, itemPerPage);
        OneLastMessagesResponseDto oneLastMessagesResponseDto =
                socketIOService.getOneLastMessageFromEverybodyFull(offset, itemPerPage);
        return ResponseEntity.ok(oneLastMessagesResponseDto);
    }

    @Override
    public ResponseEntity<DialogMessagesResponse> getDialogMessages(Long interlocutorId, String query, int offset, int itemPerPage) {

        LOGGER.info("start etDialogMessages: interlocutorId={},  query={}, offset={}, itemPerPage={}",
                interlocutorId,  query, offset, itemPerPage);
        DialogMessagesResponse dialogMessagesResponse = socketIOService.getDialogMessages(interlocutorId, query, offset, itemPerPage);
        return ResponseEntity.ok(dialogMessagesResponse);
    }

}
