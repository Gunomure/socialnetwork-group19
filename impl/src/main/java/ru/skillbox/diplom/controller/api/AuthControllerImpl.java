package ru.skillbox.diplom.controller.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.AuthController;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;
import ru.skillbox.diplom.service.AuthService;

@RestController
@Slf4j
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("controller authenticate: {}", request);
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @Override
    public ResponseEntity<?> logout() {
            return ResponseEntity.ok(authService.logout());
    }
}
