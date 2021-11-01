package ru.skillbox.diplom.service;

import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.CityMapper;
import ru.skillbox.diplom.mappers.CountryMapper;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.response.loginResponse.LoginResponse;
import ru.skillbox.diplom.repository.PersonRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final PersonRepository personRepository;

    public AuthService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public LoginResponse login (String email, String token){
        Person person;
        Optional<Person> optionalPerson = personRepository.findByEmail(email);
        if (optionalPerson.isPresent()){
            person = optionalPerson.get();
        } else throw new EntityNotFoundException("Пользователь с таким электронным адресом не найден");
        LoginResponse response = new LoginResponse();
        PersonDTO personDTO = PersonMapper.getInstance().toPersonDTO(person);
        CityDTO cityDTO = CityMapper.getInstance().toCityDTO(person.getCity());
        CountryDTO countryDTO = CountryMapper.getInstance().toCountryDTO(person.getCountry());
        response.setCity(cityDTO);
        response.setCountry(countryDTO);
        response.setPerson(personDTO);
        response.setTimestamp(ZonedDateTime.now());
        response.setToken(token);
        response.setBlocked(person.getIsBlocked() != null && person.getIsBlocked());
        if (person.getLastOnlineTime() != null) response.setLastOnlineTime(person.getLastOnlineTime().toInstant().getEpochSecond() * 1000);
        response.setMessagePermission(person.getPermission().toString() == null ? MessagePermission.ALL.toString() : person.getPermission().toString());
        return response;
    }
}
