package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PasswordRecoveryRequest;
import ru.skillbox.diplom.model.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RequestRegister;

@CrossOrigin
@RequestMapping("/api/v1/account")
public interface AccountController {

    @PutMapping("/password/recovery")
    CommonResponse recoverPassword(@RequestBody PasswordRecoveryRequest passwordRecoveryRequest);

    @PutMapping("/password/set")
    CommonResponse setPassword(@RequestBody PasswordSetRequest passwordSetRequest);

    @PostMapping("/register")
    ResponseEntity register(@RequestBody RequestRegister registerRequest);
}
