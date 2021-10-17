package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NewPersonDto {
    private String firstName;
    private String lastName;
    private String email;
    private String confirmationCode;
    private String password;
}
