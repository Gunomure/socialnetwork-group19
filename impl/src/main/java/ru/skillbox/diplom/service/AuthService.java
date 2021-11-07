package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

@Service
public class AuthService {
    private final static Logger LOGGER = LogManager.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public CommonResponse<PersonDto> login(String email, String token, RefreshToken refreshToken) {
        LOGGER.info("start login: email={}, token={}", email, token);

        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );
        PersonDto personDTO = personMapper.toPersonDTO(person);
        personDTO.setToken(token);
        personDTO.setRefreshToken(refreshToken.getToken());
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        LOGGER.info("finish getProfileData");

        return response;
    }
}
