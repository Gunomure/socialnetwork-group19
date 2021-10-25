package ru.skillbox.diplom.model;

import lombok.Data;

@Data
public class PasswordSetRequest {
    private String token;
    private String password;
}
