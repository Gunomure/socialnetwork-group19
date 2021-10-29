package ru.skillbox.diplom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@Setter
public class PersonDTO {

    private Long id;

    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "birth_date")
    private Long birthDate;

    @JsonProperty(value = "reg_date")
    private Long registrationDate;

    private CityDTO city;

    private CountryDTO country;

    private String email;

    private String phone;

    private String photo;

    @JsonProperty(value = "about")
    private String description;

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate == null ? null : birthDate.getTime();
    }

    public void setRegistrationDate(ZonedDateTime registrationDate) {
        this.registrationDate = registrationDate == null ? null : registrationDate.toInstant().getEpochSecond() * 1000;
    }
}
