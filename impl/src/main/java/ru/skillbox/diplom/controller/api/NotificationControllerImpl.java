package ru.skillbox.diplom.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.NotificationController;
import ru.skillbox.diplom.service.NotificationService;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(notificationService.getNotifications());
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id, String type) {
        return ResponseEntity.ok(notificationService.changeStatus(id, type));
    }
}
