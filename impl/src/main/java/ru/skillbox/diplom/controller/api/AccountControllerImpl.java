package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.AccountController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.request.PasswordRecoveryRequest;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.model.response.PasswordRecoveryResponse;
import ru.skillbox.diplom.model.response.RegisterResponse;
import ru.skillbox.diplom.service.AccountService;
import ru.skillbox.diplom.util.TimeUtil;

@RestController
public class AccountControllerImpl implements AccountController {
    private final static Logger LOGGER = LogManager.getLogger(AccountControllerImpl.class);

    private final AccountService accountService;
    private final AuthenticationManager authenticationManager;

    public AccountControllerImpl(AccountService accountService, AuthenticationManager authenticationManager) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public CommonResponse<PasswordRecoveryResponse> recoverPassword(@RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
        LOGGER.info("recoverPassword: {}", passwordRecoveryRequest.toString());
        accountService.sendPasswordRecoveryEmail(passwordRecoveryRequest.getEmail());
        return createResponse("Email has been sent");
    }

    @Override
    public CommonResponse<PasswordRecoveryResponse> setPassword(@RequestBody PasswordSetRequest passwordSetRequest) {
        LOGGER.info("setPassword: {}", passwordSetRequest.toString());
        accountService.setPassword(passwordSetRequest);
        return createResponse("Password has been changed");
    }

    @Override
    public CommonResponse<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        LOGGER.info("register: {}", registerRequest.toString());
        accountService.registerAccount(registerRequest);
        CommonResponse<RegisterResponse> response = new CommonResponse<>();

        response.setData(new RegisterResponse("ok"));
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        return response;
    }

    private CommonResponse<PasswordRecoveryResponse> createResponse(String message) {
        CommonResponse<PasswordRecoveryResponse> response = new CommonResponse<>();

        response.setData(new PasswordRecoveryResponse(message));
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());

        return response;
    }
}
