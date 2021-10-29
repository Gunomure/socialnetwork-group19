package ru.skillbox.diplom.model.response.logoutResponse;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.HashMap;

@Getter
@Setter
public class LogoutResponse {

   private String error = "logout success";

   private Long timestamp;

   private HashMap<String, String> data = new HashMap<>();

   public void setTimestamp(ZonedDateTime timestamp) {
      this.timestamp = timestamp.toInstant().getEpochSecond() * 1000;
   }
}
