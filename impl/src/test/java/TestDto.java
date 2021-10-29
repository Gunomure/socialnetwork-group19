import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDTO;

public class TestDto {

    @Test
    public void testPersonToPersonDto(){
        Person person = new Person();
        person.setFirstName("Ivan");
        person.setLastName("Petrov");
        person.setEmail("ivan_petrov@gmail.com");
        person.setConfirmationCode("597558");
        person.setPassword("qwerty");

        PersonDTO PersonDto = PersonMapper.getInstance().toPersonDTO(person);
        Assertions.assertEquals(person.getFirstName(), PersonDto.getFirstName());
        Assertions.assertEquals(person.getLastName(), PersonDto.getLastName());
        Assertions.assertEquals(person.getEmail(), PersonDto.getEmail());

        Person p = PersonMapper.getInstance().toPersonEntity(PersonDto);
        Assertions.assertEquals(PersonDto.getFirstName(), p.getFirstName());
        Assertions.assertEquals(PersonDto.getLastName(), p.getLastName());
        Assertions.assertEquals(PersonDto.getEmail(), p.getEmail());
    }
}
