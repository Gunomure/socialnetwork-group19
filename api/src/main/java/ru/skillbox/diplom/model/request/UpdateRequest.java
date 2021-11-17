package ru.skillbox.diplom.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateRequest {
    @JsonProperty(value = "first_name")
    private String firstName;

    @JsonProperty(value = "last_name")
    private String lastName;

    @JsonProperty(value = "birth_date")
    private Date birthDate; //TODO изменить получение даты с фронта на ZonedDateTime. В базе поменял уже.

    private String phone;

    @JsonProperty(value = "photo_id")
    private String photoId;

    @JsonProperty(value = "about")
    private String about;

    @JsonProperty(value = "town_id")
    private String townId;

    @JsonProperty(value = "country_id")
    private String countryId;

    @JsonProperty(value = "messages_permission")
    private String permission;
}
