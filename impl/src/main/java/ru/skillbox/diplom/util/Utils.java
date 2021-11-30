package ru.skillbox.diplom.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.enums.MessagePermission;

public class Utils {

    public static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String email, String password){
        return new UsernamePasswordAuthenticationToken(email, password);
    }

    public static MessagePermission parsePermission(String permission) {
        if (permission.equals("FRIEND")) return MessagePermission.FRIEND;
        else return MessagePermission.ALL;
    }
}
