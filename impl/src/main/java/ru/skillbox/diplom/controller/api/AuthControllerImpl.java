package ru.skillbox.diplom.controller.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.controller.AuthController;
import ru.skillbox.diplom.exception.TokenRefreshException;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;
import ru.skillbox.diplom.model.response.TokenRefreshResponse;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.service.RefreshTokenService;

@RestController
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {
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
