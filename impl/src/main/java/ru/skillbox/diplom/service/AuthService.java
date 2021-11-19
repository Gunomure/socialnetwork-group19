package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.exception.TokenRefreshException;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.TokenRefreshResponse;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

import javax.naming.NamingException;

@Service
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final LdapService ldapService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PersonRepository personRepository,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenService refreshTokenService, LdapService ldapService, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.ldapService = ldapService;
        this.passwordEncoder = passwordEncoder;
    }

    public CommonResponse<PersonDto> login(LoginRequest request) {

        if (ldapService.authUser(request.getEmail(), request.getPassword())){
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
        else {
            CommonResponse<PersonDto> response = new CommonResponse<>();
            response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
            response.setError("no successful login");
            return response;
        }
    }

    public CommonResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        CommonResponse<TokenRefreshResponse> response = new CommonResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        try {
            response.setData(validateAndReturnNewJwtAndRefreshToken(request.getRefreshToken()));
            response.setError("No error");
            return response;
        } catch (TokenRefreshException | NamingException e) {
            response.setError(e.getMessage());
            return response;
        }
    }

    private TokenRefreshResponse validateAndReturnNewJwtAndRefreshToken(String refreshToken) throws NamingException {
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
        CommonResponse<MessageResponse> response = new CommonResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(new MessageResponse("ok"));
        LOGGER.info("success logout");
        return response;
    }


}
