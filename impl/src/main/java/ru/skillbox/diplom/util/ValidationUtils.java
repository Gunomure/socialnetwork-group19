package ru.skillbox.diplom.util;

import ru.skillbox.diplom.exception.BadCredentialsException;

public class ValidationUtils {

    private static final String emailRegex = "^[a-z0-9](\\.?[a-z0-9_-]){0,}@[a-z0-9-]+\\.([a-z]{1,6}\\.)?[a-z]{2,}$";
    private static final String nameRegex = "[A-ZА-Яа-яА-Яa-zA-Z]";
    private static final String passwordRegex = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";
    private static final String phoneRegex = "^(\\+|\\d)*(\\(\\d{3}\\)\\s*)*\\d{3}(-{0,1}|\\s{0,1})\\d{2}(-{0,1}|\\s{0,1})\\d{2}$";

    public static void validateEmail(String email) {
        if (!email.matches(emailRegex))
            throw new BadCredentialsException(String.format("Wrong email format: %s", email));
    }

    public static void validateName(String name) {
        if (!name.matches(nameRegex))
            throw new BadCredentialsException(String.format("Wrong name format: %s", name));
    }

    public static void validatePassword(String password) {
        if (!password.matches(passwordRegex))
            throw new BadCredentialsException("Wrong password format");
    }

    public static void validatePhone(String phone) {
        if(phone.matches(phoneRegex))
            throw new BadCredentialsException(String.format("Wrong phone format: %s", phone));
    }




}
