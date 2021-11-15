package ru.skillbox.diplom.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
