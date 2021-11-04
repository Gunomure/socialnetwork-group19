package ru.skillbox.diplom.model.request;


import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String passwd1;
    private String passwd2;
    private String firstName;
    private String lastName;
    private String code;
}