package ru.skillbox.diplom.model.response;

import lombok.Data;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.TagDto;

import java.util.List;

@Data
public class TagListResponse extends CommonResponse<List<TagDto>> {
    private int total;
    private int offset;
    private int perPage;
}
