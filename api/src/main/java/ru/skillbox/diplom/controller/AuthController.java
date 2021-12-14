package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;

@CrossOrigin
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest request);

    @PostMapping("/logout")
    ResponseEntity<?> logout();

    @PostMapping("/refresh")
    ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request);
}
