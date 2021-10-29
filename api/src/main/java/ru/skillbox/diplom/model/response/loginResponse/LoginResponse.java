package ru.skillbox.diplom.model.response.loginResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spotify.docker.client.shaded.com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.skillbox.diplom.model.CityDTO;
import ru.skillbox.diplom.model.CountryDTO;
import ru.skillbox.diplom.model.PersonDTO;

import java.time.ZonedDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private final String error = "login success";

    private Long timestamp;

    @JsonProperty(value = "data")
    private PersonDTO person;

    private CityDTO city;

    private CountryDTO country;

    @JsonProperty(value = "message_permission")
    private String messagePermission;

    @JsonProperty(value = "last_online_time")
    private Long lastOnlineTime;

    @JsonProperty(value = "is_blocked")
    private boolean isBlocked;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp.toInstant().getEpochSecond() * 1000;
    }
}