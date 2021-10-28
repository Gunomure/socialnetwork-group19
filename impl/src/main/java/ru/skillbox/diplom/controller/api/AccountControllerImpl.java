package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.AccountController;
import ru.skillbox.diplom.exception.CustomErrorResponse;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.RequestRegister;
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
    public CommonResponse recoverPassword(@RequestBody PasswordRecoveryRequest passwordRecoveryRequest) {
        LOGGER.info("recoverPassword: {}", passwordRecoveryRequest.toString());
        accountService.sendPasswordRecoveryEmail(passwordRecoveryRequest.getEmail());
        return createResponse("Email has been sent");
    }

    @Override
    public CommonResponse setPassword(@RequestBody PasswordSetRequest passwordSetRequest) {
        LOGGER.info("setPassword: {}", passwordSetRequest.toString());
        accountService.setPassword(passwordSetRequest);
        return createResponse("Password has been changed");
    }

    @Override
    public ResponseEntity<?> register(@RequestBody RequestRegister registerRequest) {
        LOGGER.info("register: {}", registerRequest.toString());
        accountService.registerAccount(registerRequest);
        return ResponseEntity.ok(createResponse("ok"));
    }

    private CommonResponse createResponse(String message) {
        CommonResponse response = new CommonResponse();

        response.setData(new PasswordRecoveryResponse(message));
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());

        return response;
    }

}
