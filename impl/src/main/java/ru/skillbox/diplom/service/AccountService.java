package ru.skillbox.diplom.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.UserDto;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.enums.UserType;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AccountService {

    @Value("${email_service.path}")
    private String EMAIL_SERVICE_PATH;

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    RestTemplate restTemplate = new RestTemplate();

    public AccountService(PersonRepository personRepository,
                          PasswordEncoder passwordEncoder
                          ) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendPasswordRecoveryEmail(String receiverEmail) {
        Person currentUser = personRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", receiverEmail)));
        UUID confirmationCode = UUID.randomUUID();
        currentUser.setConfirmationCode(confirmationCode.toString());
        personRepository.save(currentUser);
        restTemplate.postForEntity(EMAIL_SERVICE_PATH,
                new UserDto(currentUser.getEmail(), currentUser.getConfirmationCode()),
                UserDto.class);
    }

    public void setPassword(PasswordSetRequest passwordSetRequest) {
        Person currentUser = personRepository.findByConfirmationCode(passwordSetRequest.getToken()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User not found by confirmation code: %s", passwordSetRequest.getToken())));

        currentUser.setPassword(passwordEncoder.encode(passwordSetRequest.getPassword()));
        personRepository.save(currentUser);
    }

    public void registerAccount(RegisterRequest registerRequest) {
        boolean personExists = personRepository.isExists(registerRequest.getEmail());
        if (!personExists) {
            Person user = new Person();
            user.setEmail(registerRequest.getEmail());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPassword(passwordEncoder.encode(registerRequest.getPasswd1()));
            user.setRegistrationDate(ZonedDateTime.now());
            user.setPermission(MessagePermission.ALL);
            user.setType(UserType.USER);
            user.setConfirmationCode(registerRequest.getCode());
            user.setLastOnlineTime(ZonedDateTime.now());
            user.setBirthDate(TimeUtil.now().minusYears(30)); //TODO repair it!
            personRepository.save(user);
        } else {
            throw new BadRequestException(String.format("User with email %s already exists",
                    registerRequest.getEmail()));
        }
    }
}
