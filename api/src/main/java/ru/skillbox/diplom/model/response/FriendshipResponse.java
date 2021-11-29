package ru.skillbox.diplom.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.FriendshipResponseDto;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FriendshipResponse extends CommonResponse<List<FriendshipResponseDto>> {
    private int total;
    private int offset;
    private int perPage;
}
