package ru.skillbox.diplom.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.CommonResponse;

@Getter
@Setter
@AllArgsConstructor
public class LogoutResponse extends CommonResponse {
   private String message;
}
