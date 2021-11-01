package ru.skillbox.diplom.controller.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.User;
import ru.skillbox.diplom.model.request.RequestLogin;
import ru.skillbox.diplom.model.response.loginResponse.InvalidLoginResponse;
import ru.skillbox.diplom.model.response.logoutResponse.InvalidLogoutResponse;
import ru.skillbox.diplom.model.response.logoutResponse.LogoutResponse;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.util.Utils;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager,
                                          UserRepository userRepository,
                                          JwtTokenProvider jwtTokenProvider,
                                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody RequestLogin request) {
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
            String token = jwtTokenProvider.createToken(request.getEmail(), user.getType().name());
            return ResponseEntity.ok(authService.login(request.getEmail(), token));
        } catch (AuthenticationException | EntityNotFoundException e) {
            return new ResponseEntity<>(new InvalidLoginResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityContextHolder.clearContext();
//            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
//            securityContextLogoutHandler.logout(request, response, null);
            LogoutResponse logoutResponse = new LogoutResponse();
            logoutResponse.setTimestamp(ZonedDateTime.now());
            logoutResponse.getData().put("message", "ok");
            return ResponseEntity.ok(logoutResponse);
        } catch (Exception e ){
            return new ResponseEntity<>(new InvalidLogoutResponse(), HttpStatus.BAD_REQUEST);
        }
    }
}
