package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.*;
import ru.skillbox.diplom.model.request.AccountNotificationsBody;
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

    @PutMapping("/password/change")
    CommonResponse<PasswordRecoveryResponse> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest);

    @PutMapping("/email/change")
    CommonResponse<PasswordRecoveryResponse> changeEmail(@RequestBody EmailChangeRequest emailChangeRequest);

    @PostMapping("/register")
    CommonResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest);

    @GetMapping("/notifications")
    CommonResponse<?> getAccountNotifications();

    @PutMapping("/notifications")
    CommonResponse<?> putAccountNotifications(@RequestBody AccountNotificationsBody body);
}
