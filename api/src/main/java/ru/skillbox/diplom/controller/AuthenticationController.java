package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.request.LoginRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RequestMapping("/api/v1/auth")
public interface AuthenticationController {

    @PostMapping("/login")
    ResponseEntity<?> authenticate(@RequestBody LoginRequest request);

    @PostMapping("/logout")
    ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response);

}
