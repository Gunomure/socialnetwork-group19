package ru.skillbox.diplom.model.response.dialogs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class PersonInfoShortDto {

    private static final ZoneId ZONE_UTC = ZoneId.of("UTC");
    //TODO: maybe remove in one place, but problem - cycle reference
    public PersonInfoShortDto(Long id, String photo, String firstName, java.sql.Timestamp lastOnlineTime) {
        this.id = id;
        this.photo = photo;
        this.firstName = firstName;
        if (lastOnlineTime == null) {
            this.lastOnlineTime = null;
        }
        else
        {
            //TODO: need to check. Is it correct?
            ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastOnlineTime.getTime()),
                    ZONE_UTC);
            this.lastOnlineTime = ZonedDateTime.ofInstant(zdt.toInstant(), ZONE_UTC).toEpochSecond();
        }
    }

    Long  id;
    String photo;
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_online_time")
    Long lastOnlineTime;
}
