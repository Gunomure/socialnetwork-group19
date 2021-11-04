package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.PasswordRecoveryRequest;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.model.response.PasswordRecoveryResponse;
import ru.skillbox.diplom.model.response.RegisterResponse;

@CrossOrigin
@RequestMapping("/api/v1/account")
public interface AccountController {

    @PutMapping("/password/recovery")
    CommonResponse<PasswordRecoveryResponse> recoverPassword(@RequestBody PasswordRecoveryRequest passwordRecoveryRequest);

    @PutMapping("/password/set")
    CommonResponse<PasswordRecoveryResponse> setPassword(@RequestBody PasswordSetRequest passwordSetRequest);

    @PostMapping("/register")
    CommonResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest);
}
