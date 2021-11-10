package ru.skillbox.diplom.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.skillbox.diplom.model.CommonResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedsResponse<T> extends CommonResponse<T> {
    private int total;
    private int offset;
    private int perPage;
}