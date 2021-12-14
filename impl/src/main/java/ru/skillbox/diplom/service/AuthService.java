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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skillbox.diplom.config.security.jwt.JwtTokenProvider;
import ru.skillbox.diplom.exception.TokenRefreshException;
import ru.skillbox.diplom.exception.UnauthorizedException;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.request.LoginRequest;
import ru.skillbox.diplom.model.request.TokenRefreshRequest;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.TokenRefreshResponse;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(PersonRepository personRepository,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       RefreshTokenService refreshTokenService, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public CommonResponse<PersonDto> login(LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();
        LOGGER.info("AuthService: Start login get data: email={}, password=ХренПокажем", email);
        Person person = personRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));
        checkUser(person.getPassword(), password);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtTokenProvider.createToken(email, person.getType().name());
        String refreshToken = refreshTokenService.createRefreshToken(person.getId());
        PersonDto personDTO = personMapper.toPersonDTO(person).setToken(token).setRefreshToken(refreshToken);
        CommonResponse<PersonDto> response = new CommonResponse<>("",TimeUtil.getCurrentTimestampUtc(), personDTO);
        LOGGER.info("AuthService: Finish successful login: email={}, token=ХренПокажем", email);

        return response;
    }

    private void checkUser(String userPass, String password) {
        LOGGER.info("AuthService: start checkUser()");
        if(passwordEncoder.matches(password, userPass)){
            LOGGER.info("AuthService: checkUser() successful check");
        } else {
            throw new UsernameNotFoundException("Password is incorrect");
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
                            //refreshTokenService.deleteByUserId(user.getId());
                            String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());
                            return new TokenRefreshResponse(newJwtToken, newRefreshToken);
                        }
                ).orElseThrow(() -> new TokenRefreshException(refreshToken,
                        "Refresh token is not in database!"));
    }

    public CommonResponse<MessageResponse> logout() {
        SecurityContextHolder.clearContext();
        CommonResponse<MessageResponse> response = new CommonResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(new MessageResponse("ok"));
        LOGGER.info("success logout");
        return response;
    }

    public Person getCurrentUser(HttpServletRequest request) {
        return getCurrentUser(getCurrentUserEmail(request));
    }

    public Person getCurrentUser(String userEmail) {
        return personRepository.findByEmail(userEmail).orElseThrow(
                () -> new UnauthorizedException(String.format("User %s unauthorized", userEmail))
        );
    }

    public String getCurrentUserEmail(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        return jwtTokenProvider.getUsername(token);
    }
}
