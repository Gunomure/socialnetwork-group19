package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

@Service
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(PersonRepository personRepository,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenService refreshTokenService) {
        this.personRepository = personRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public CommonResponse<PersonDto> login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        LOGGER.info("login data: email={}, password={}", email, password);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        Person person = personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        String token = jwtTokenProvider.createToken(email, person.getType().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(person.getId());

        LOGGER.info("start login: email={}, token={}", email, token);

        PersonDto personDTO = personMapper.toPersonDTO(person);
        personDTO.setToken(token);
        personDTO.setRefreshToken(refreshToken.getToken());
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        LOGGER.info("finish getProfileData");

        return response;
    }

    public CommonResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        CommonResponse<TokenRefreshResponse> response = new CommonResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        try {
            response.setData(validateAndReturnNewJwtAndRefreshToken(request.getRefreshToken()));
            response.setError("No error");
            return response;
        } catch (TokenRefreshException e) {
            response.setError(e.getMessage());
            return response;
        }
    }

    private TokenRefreshResponse validateAndReturnNewJwtAndRefreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                            String newJwtToken = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
                            refreshTokenService.deleteByUserId(user.getId());
                            String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();
                            return new TokenRefreshResponse(newJwtToken, newRefreshToken);
                        }
                ).orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Refresh token is not in database!"));
    }

    public CommonResponse<MessageResponse> logout(){
        SecurityContextHolder.clearContext();
        CommonResponse<LogoutResponse> response = new CommonResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(new LogoutResponse("ok"));
        LOGGER.info("success logout");
        return response;
    }


}
