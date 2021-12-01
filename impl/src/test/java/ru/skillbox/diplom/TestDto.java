package ru.skillbox.diplom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.repository.PersonRepository;

import java.time.ZonedDateTime;
import java.util.Optional;

@Transactional
public class TestDto extends AbstractIntegrationTest {
    @Value("${embedded-postgresql.password}")
    private String password;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testPersonToPersonDtoTest() {
        Person person = new Person();
        person.setFirstName("Ivan");
        person.setLastName("Petrov");
        person.setEmail("ivan_petrov@gmail.com");
        person.setConfirmationCode("597558");
        person.setPassword("qwerty");
        person.setLastOnlineTime(ZonedDateTime.now());

        PersonDto PersonDto = personMapper.toPersonDTO(person);
        Assertions.assertEquals(person.getFirstName(), PersonDto.getFirstName());
        Assertions.assertEquals(person.getLastName(), PersonDto.getLastName());
        Assertions.assertEquals(person.getEmail(), PersonDto.getEmail());

        Person p = personMapper.toPersonEntity(PersonDto);
        Assertions.assertEquals(PersonDto.getFirstName(), p.getFirstName());
        Assertions.assertEquals(PersonDto.getLastName(), p.getLastName());
        Assertions.assertEquals(PersonDto.getEmail(), p.getEmail());
    }

    @Test
    @Disabled
    void testDatabaseTest() throws Exception {
        System.out.println("!!!!" + password);
        loginAsUser();

        Optional<Person> person = personRepository.findById(1L);
        System.out.println(person.get());
        System.out.println("test");
        logout();
        System.out.println("test2");
    }
}
