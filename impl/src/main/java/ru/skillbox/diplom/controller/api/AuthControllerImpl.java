package ru.skillbox.diplom.controller.api;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.controller.AuthController;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.User;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.response.LogoutResponse;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.util.TimeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthControllerImpl implements AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public AuthControllerImpl(AuthenticationManager authenticationManager,
                              UserRepository userRepository,
                              JwtTokenProvider jwtTokenProvider,
                              AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authService = authService;
    }

    @Override
    public CommonResponse<PersonDto> authenticate(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        String token = jwtTokenProvider.createToken(request.getEmail(), user.getType().name());
        return authService.login(request.getEmail(), token);
    }

    @Override
    public CommonResponse<LogoutResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            SecurityContextHolder.clearContext();
//            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
//            securityContextLogoutHandler.logout(request, response, null);
            CommonResponse<LogoutResponse> commonResponse = new CommonResponse<>();
            commonResponse.setTimestamp(TimeUtil.getCurrentTimestampUtc());
            commonResponse.setData(new LogoutResponse("ok"));
            return commonResponse;
        } catch (Exception e) {
            throw new BadRequestException("Logout error");
        }
    }
}
