package ru.skillbox.diplom.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class Utils {

    public static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String email, String password){
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
