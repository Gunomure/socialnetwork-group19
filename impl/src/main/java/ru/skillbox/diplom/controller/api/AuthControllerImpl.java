package ru.skillbox.diplom.controller.api;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.controller.AuthController;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.TokenRefreshException;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.User;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;
import ru.skillbox.diplom.model.response.LogoutResponse;
import ru.skillbox.diplom.model.response.TokenRefreshResponse;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.service.RefreshTokenService;
import ru.skillbox.diplom.util.TimeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthControllerImpl implements AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    public AuthControllerImpl(JwtTokenProvider jwtTokenProvider,
                              AuthService authService,
                              RefreshTokenService refreshTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @Override
    public ResponseEntity<?> logout() {
            return ResponseEntity.ok(authService.logout());
    }
}
