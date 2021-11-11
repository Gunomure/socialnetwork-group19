package ru.skillbox.diplom.model.response;

import lombok.Data;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;

import java.util.List;

@Data
public class UsersSearchResponse extends CommonResponse<List<PersonDto>> {
    private int total;
    private int offset;
    private int itemPerPage;
}
