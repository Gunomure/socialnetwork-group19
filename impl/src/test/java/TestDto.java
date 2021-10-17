import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.NewPersonDto;
import ru.skillbox.diplom.model.Person;

public class TestDto {

    @Test
    public void testPersonToPersonDto(){
        Person person = new Person();
        person.setFirstName("Ivan");
        person.setLastName("Petrov");
        person.setEmail("ivan_petrov@gmail.com");
        person.setConfirmationCode("597558");
        person.setPassword("qwerty");

        NewPersonDto newPersonDto = PersonMapper.getInstance().toDto(person);
        Assertions.assertEquals(person.getFirstName(), newPersonDto.getFirstName());
        Assertions.assertEquals(person.getLastName(), newPersonDto.getLastName());
        Assertions.assertEquals(person.getConfirmationCode(), newPersonDto.getConfirmationCode());
        Assertions.assertEquals(person.getEmail(), newPersonDto.getEmail());
        Assertions.assertEquals(person.getPassword(), newPersonDto.getPassword());

        Person p = PersonMapper.getInstance().toEntity(newPersonDto);
        Assertions.assertEquals(newPersonDto.getFirstName(), p.getFirstName());
        Assertions.assertEquals(newPersonDto.getLastName(), p.getLastName());
        Assertions.assertEquals(newPersonDto.getEmail(), p.getEmail());
        Assertions.assertEquals(newPersonDto.getPassword(), p.getPassword());
        Assertions.assertEquals(newPersonDto.getConfirmationCode(), p.getConfirmationCode());
    }
}
