package ru.skillbox.diplom.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountNotificationsDto {
    private String notificationType;
    private Boolean enable;
}
