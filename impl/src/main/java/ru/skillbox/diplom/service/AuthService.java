package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

@Service
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public CommonResponse<PersonDto> login(String email, String token) {
        LOGGER.info("start login: email={}, token={}", email, token);

        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );

        PersonDto personDTO = PersonMapper.getInstance().toPersonDTO(person);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        LOGGER.info("finish getProfileData");

        return response;
    }
}
