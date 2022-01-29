package ru.skillbox.diplom.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationEmailDto {

    private String email;

    private String notificationPath;
}
