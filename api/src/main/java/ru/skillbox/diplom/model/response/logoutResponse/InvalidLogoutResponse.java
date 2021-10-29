package ru.skillbox.diplom.model.response.logoutResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidLogoutResponse {

   private String error =  "invalid_request";

   @JsonProperty("error_description")
   private String errorDescription = "something wrong";
}
