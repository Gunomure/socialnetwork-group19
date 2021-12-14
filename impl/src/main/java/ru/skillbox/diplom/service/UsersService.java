package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.repository.PostRepository;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static ru.skillbox.diplom.util.TimeUtil.getCurrentTimestampUtc;

@Service
@Transactional
public class UsersService {
    private final static Logger LOGGER = LogManager.getLogger(UsersService.class);

    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);
    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    public UsersService(PersonRepository personRepository,
                        PostRepository postRepository) {
        this.personRepository = personRepository;
        this.postRepository = postRepository;
    }

    public CommonResponse<PersonDto> getProfileData() {
        LOGGER.info("start getProfileData");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );

        PersonDto personDTO = personMapper.toPersonDTO(person);
        List<PostDto> postDtos = postMapper.convertToListPostDto(postRepository.findByAuthorId(person));
        personDTO.setPosts(postDtos);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDTO);
        response.setTimestamp(getCurrentTimestampUtc());
        LOGGER.info("finish getProfileData");

        return response;
    }

    public CommonResponse<PersonDto> updateProfileData(UpdateRequest data) {
        LOGGER.info("start updateProfileData");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email))
        );


        person.setFirstName(data.getFirstName());
        person.setLastName(data.getLastName());
        person.setBirthDate(ZonedDateTime.ofInstant(data.getBirthDate().toInstant(), ZoneId.of("UTC"))); //TODO изменить получение даты с фронта на ZonedDateTime
        /*Optional<City> city = cityRepository.findByTitle(data.getTownId());
        if (city.isPresent()) person.setCity(city.get());
        else throw new EntityNotFoundException(String.format("City %s not found", data.getTownId()));
        Optional<Country> country = countryRepository.findByTitle(data.getCountryId());
        if (country.isPresent()) person.setCountry(country.get());
        else throw new EntityNotFoundException(String.format("Country %s not found", data.getCountryId()));*/
        person.setPhone(data.getPhone());
        person.setDescription(data.getAbout());
        /*person.setPermission(Utils.parsePermission(data.getPermission()));*/

        PersonDto responseData = personMapper.toPersonDTO(person);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setTimestamp(getCurrentTimestampUtc());
        response.setData(responseData);
        LOGGER.info("finish updateProfileData");
        return response;
    }

    public UsersSearchResponse searchUsers(String firstName, String lastName,
                                           Integer ageFrom, Integer ageTo,
                                           String country, String city,
                                           Integer offset, Integer itemPerPage) {
        LOGGER.info("start searchUsers: firstName={}, lastName={}, " +
                        "ageFrom={}, ageTo={}, country={}, city={}, offset={}, itemPerPage={}",
                firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage);

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
        List<PersonDto> persons = personMapper.toListPersonDTO(personEntities);

        UsersSearchResponse response = new UsersSearchResponse();
        response.setData(persons);
        response.setTotal(persons.size());
        response.setTimestamp(getCurrentTimestampUtc());
        response.setOffset(response.getOffset());
        response.setItemPerPage(itemPerPage);
        LOGGER.info("finish searchUsers");

        return response;
    }

    public CommonResponse<PersonDto> searchUserById(Long id) {
        LOGGER.info("start searchUser: id={}", id);

        Person personEntity = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException("User not found")
        );
        PersonDto personDto = personMapper.toPersonDTO(personEntity);
        List<PostDto> postDtos = postMapper.convertToListPostDto(postRepository.findByAuthorId(personEntity));
        personDto.setPosts(postDtos);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        response.setData(personDto);
        response.setTimestamp(getCurrentTimestampUtc());
        LOGGER.info("finish searchUser: id={}", id);

        return response;
    }
}
