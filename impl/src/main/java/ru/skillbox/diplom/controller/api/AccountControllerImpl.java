package ru.skillbox.diplom.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import ru.skillbox.diplom.controller.AccountController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.*;
import ru.skillbox.diplom.model.request.AccountNotificationsBody;
import ru.skillbox.diplom.model.request.PasswordRecoveryRequest;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.model.response.PasswordRecoveryResponse;
import ru.skillbox.diplom.model.response.RegisterResponse;
import ru.skillbox.diplom.service.AccountService;
import ru.skillbox.diplom.util.TimeUtil;

@RestController
@RequiredArgsConstructor
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;


    @Override
    public CommonResponse<PasswordRecoveryResponse> recoverPassword(@RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
        accountService.sendPasswordRecoveryEmail(passwordRecoveryRequest.getEmail());
        return createResponse("Email has been sent");
    }

    @Override
    public CommonResponse<PasswordRecoveryResponse> setPassword(@RequestBody PasswordSetRequest passwordSetRequest) {
        accountService.setPassword(passwordSetRequest);
        return createResponse("Password has been changed");
    }

    @Override
    public CommonResponse<PasswordRecoveryResponse> changePassword(PasswordChangeRequest passwordChangeRequest) {
        accountService.changePassword(passwordChangeRequest);
        return createResponse("Password has been changed");
    }

    @Override
    public CommonResponse<PasswordRecoveryResponse> changeEmail(EmailChangeRequest emailChangeRequest) {
        accountService.changeEmail(emailChangeRequest);
        return createResponse("Email has been changed");
    }

    @Override
    public CommonResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        accountService.registerAccount(registerRequest);
        CommonResponse<RegisterResponse> response = new CommonResponse<>();

        response.setData(new RegisterResponse("ok"));
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        return response;
    }

    @Override
    public CommonResponse<?> getAccountNotifications() {
        return accountService.getAccountNotifications();
    }

    @Override
    public CommonResponse<?> putAccountNotifications(AccountNotificationsBody body) {
        return accountService.putAccountNotifications(body);
    }

    private CommonResponse<PasswordRecoveryResponse> createResponse(String message) {
        CommonResponse<PasswordRecoveryResponse> response = new CommonResponse<>();

        response.setData(new PasswordRecoveryResponse(message));
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());

        return response;
    }
}
