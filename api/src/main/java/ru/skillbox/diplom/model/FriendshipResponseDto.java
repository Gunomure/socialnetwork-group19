package ru.skillbox.diplom.model;

import lombok.Data;
import ru.skillbox.diplom.model.response.FriendshipCodeDto;

@Data
public class FriendshipResponseDto extends PersonDto {
    private FriendshipCodeDto friendshipStatus;
}
