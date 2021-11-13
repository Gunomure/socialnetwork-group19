package ru.skillbox.diplom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.NotificationController;
import ru.skillbox.diplom.model.CommonResponse;

@RestController
public class NotificationControllerImpl implements NotificationController {


    @Override
    public ResponseEntity<?> getNotifications(Integer offset, Integer itemPerPage) {
        return ResponseEntity.ok(new CommonResponse());
    }
}
