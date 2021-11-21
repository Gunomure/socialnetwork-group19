package ru.skillbox.diplom.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.CommonResponse;

@Data
@AllArgsConstructor
public class MessageResponse {
   private String message;
}
