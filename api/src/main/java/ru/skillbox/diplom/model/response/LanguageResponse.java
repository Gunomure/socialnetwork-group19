package ru.skillbox.diplom.model.response;

import lombok.Data;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.LanguageDto;

import java.util.List;

@Data
public class LanguageResponse extends CommonResponse<List<LanguageDto>> {
    private int total;
    private int offset;
    private int perPage;
}

/*
{
  "error": "string",
  "timestamp": 1559751301818,
  "total": 0,
  "offset": 0,
  "perPage": 20,
  "data": [
    {
      "id": 1,
      "title": "Русский"
    }
  ]
}
 */