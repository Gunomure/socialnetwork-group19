package ru.skillbox.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.mappers.PostMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.UpdateRequest;
import ru.skillbox.diplom.model.response.UsersSearchResponse;
import ru.skillbox.diplom.repository.CityRepository;
import ru.skillbox.diplom.repository.CountryRepository;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.repository.PostRepository;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.skillbox.diplom.util.TimeUtil.getCurrentTimestampUtc;

@Slf4j
@Service
@Transactional
public class UsersService {

    @Value("${values.avatar.deleted}")
    private String DELETED;

    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public UsersService(PersonRepository personRepository,
                        PostRepository postRepository,
                        CityRepository cityRepository,
                        CountryRepository countryRepository) {
        this.personRepository = personRepository;
        this.postRepository = postRepository;
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    public CommonResponse<PersonDto> getProfileData() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );
        PersonDto personDTO = personMapper.toPersonDTO(person);
        SpecificationUtil<Post> spec = new SpecificationUtil<>();
        List<Post> personalPosts = postRepository.findAll(
                        Specification.
                                where(spec.equals(Post_.IS_BLOCKED, false)).
                                and(spec.equals(Post_.AUTHOR_ID, Person_.EMAIL, email)),
                        Sort.by(Sort.Direction.DESC, "time"));
        List<PostDto> postDtos = postMapper.convertToListPostDto(personalPosts);
        personDTO.setPosts(postDtos);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(getCurrentTimestampUtc());

        return response;
    }

    public CommonResponse<PersonDto> updateProfileData(UpdateRequest data) {


        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );

        person.setFirstName(data.getFirstName());
        person.setLastName(data.getLastName());
        person.setBirthDate(ZonedDateTime.ofInstant(data.getBirthDate().toInstant(), ZoneId.of("UTC"))); //TODO ???????????????? ?????????????????? ???????? ?? ???????????? ???? ZonedDateTime
        Optional<City> city = cityRepository.findByTitle(data.getCity());
        if (city.isPresent()) person.setCity(city.get());
        else throw new EntityNotFoundException(String.format("City %s not found", data.getCity()));

        Optional<Country> country = countryRepository.findById(data.getCountry());
        log.info(String.format("Country from bd %s ", data.getCountry()));
        if (country.isPresent()) person.setCountry(country.get());
        else throw new EntityNotFoundException(String.format("Country %s not found", data.getCountry()));
        person.setPhone(data.getPhone());
        person.setDescription(data.getAbout());
        PersonDto responseData = personMapper.toPersonDTO(person);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setTimestamp(getCurrentTimestampUtc());
        response.setData(responseData);
        return response;
    }

    public UsersSearchResponse searchUsers(String firstName, String lastName,
                                           Integer ageFrom, Integer ageTo,
                                           String country, String city,
                                           Integer offset, Integer itemPerPage) {
        SpecificationUtil<Person> spec = new SpecificationUtil<>();
        Specification<Person> s1 = spec.contains(Person_.FIRST_NAME, firstName);
        Specification<Person> s2 = spec.contains(Person_.LAST_NAME, lastName);
        Specification<Person> s3 = spec.between(Person_.BIRTH_DATE, ZonedDateTime.now().minusYears(ageTo),
                ZonedDateTime.now().minusYears(ageFrom));
        Specification<Person> s4 = spec.equals(Person_.COUNTRY, Country_.TITLE, country);
        Specification<Person> s5 = spec.equals(Person_.CITY, City_.TITLE, city);
        PageRequest pageRequest = PageRequest.of(offset, itemPerPage, Sort.by("firstName").ascending());

        List<Person> personEntities = personRepository.findAll(Specification.where(s1)
                                .and(s2)
                                .and(s3)
                                .and(s4)
                                .and(s5)
                        , pageRequest)
                .getContent(); // migrate Page to List

        List<PersonDto> persons = personMapper.toListPersonDTO(personEntities.stream()
                .filter(p -> p.getEmail() != null).collect(Collectors.toList()));

        UsersSearchResponse response = new UsersSearchResponse();
        response.setData(persons);
        response.setTotal(persons.size());
        response.setTimestamp(getCurrentTimestampUtc());
        response.setOffset(response.getOffset());
        response.setItemPerPage(itemPerPage);

        return response;
    }

    public CommonResponse<PersonDto> searchUserById(Long id) {
        Person personEntity = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException("User not found")
        );
        PersonDto personDto = personMapper.toPersonDTO(personEntity);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDto);
        response.setTimestamp(getCurrentTimestampUtc());

        return response;
    }

    public CommonResponse<PersonDto> deleteProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );
        person.setDeleteDate(ZonedDateTime.now().plusDays(30));
        person.setPhoto(DELETED);
        personRepository.save(person);

        PersonDto personDTO = personMapper.toPersonDTO(person);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(getCurrentTimestampUtc());

        return response;
    }

    @Scheduled(cron = "0 0 0 ? * *")
    public void sendBirthdayEvent() {
        List<Person> userList = personRepository.findByDeleteDateLessThanEqual(ZonedDateTime.now()).orElse(new ArrayList<>());
        if (!userList.isEmpty()) {
            for (Person p : userList) {
                p.setEmail(null);
                p.setDeleteDate(null);
                personRepository.save(p);
            }
        }
    }
}
