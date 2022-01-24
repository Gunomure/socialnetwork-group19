package ru.skillbox.diplom.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {

    private String email;

    private String confirmationCode;
}
