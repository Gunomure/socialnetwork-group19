package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.diplom.model.response.dialogs.OneLastMessagesResponseDto;
import ru.skillbox.diplom.model.response.dialogs.DialogMessagesResponse;

@CrossOrigin
@RequestMapping("/api/v1/dialogs")
@PreAuthorize("hasAuthority('developers:read')")
public interface MessageController {

    @GetMapping("")
    ResponseEntity<OneLastMessagesResponseDto> getOneLastMessageFromEverybodyFull(
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam (defaultValue = "20", required = false) int itemPerPage
    );

    @GetMapping("/messages")
    ResponseEntity<DialogMessagesResponse> getDialogMessages(
            @RequestParam(required = true) Long interlocutorId,
            @RequestParam( required = false) String query,
            @RequestParam(defaultValue = "0", required = false) int offset,
            @RequestParam (defaultValue = "20", required = false) int itemPerPage
    );
}
