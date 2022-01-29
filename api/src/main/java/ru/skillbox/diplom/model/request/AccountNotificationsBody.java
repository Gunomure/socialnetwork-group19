package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountNotificationsBody {

    @JsonProperty("notification_type")
    private String notificationType;
    private Boolean enable;
}
