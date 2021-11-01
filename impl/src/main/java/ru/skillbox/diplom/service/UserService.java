package ru.skillbox.diplom.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.CityMapper;
import ru.skillbox.diplom.mappers.CountryMapper;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.CityDTO;
import ru.skillbox.diplom.model.CountryDTO;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDTO;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.response.loginResponse.LoginResponse;
import ru.skillbox.diplom.repository.PersonRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final PersonRepository personRepository;

    public UserService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    public LoginResponse getProfileData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
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
        response.setBlocked(person.getIsBlocked() != null && person.getIsBlocked());
        if (person.getLastOnlineTime() != null) response.setLastOnlineTime(person.getLastOnlineTime().toInstant().getEpochSecond() * 1000);
        response.setMessagePermission(person.getPermission().toString() == null ? MessagePermission.ALL.toString() : person.getPermission().toString());
        return response;
    }
}
