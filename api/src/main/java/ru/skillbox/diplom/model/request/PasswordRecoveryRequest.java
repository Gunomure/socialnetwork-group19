package ru.skillbox.diplom.model.request;

import lombok.Data;

@Data
public class PasswordRecoveryRequest {
    private String email;
}
