package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.response.LogoutResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @PostMapping("/login")
    CommonResponse<PersonDto> authenticate(@RequestBody LoginRequest request);

    @PostMapping("/logout")
    CommonResponse<LogoutResponse> logout(HttpServletRequest request, HttpServletResponse response);
}
