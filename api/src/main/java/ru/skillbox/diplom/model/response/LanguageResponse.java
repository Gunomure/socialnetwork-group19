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