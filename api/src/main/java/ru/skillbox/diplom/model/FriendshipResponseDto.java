package ru.skillbox.diplom.model;

import lombok.Data;
import ru.skillbox.diplom.model.enums.FriendshipCode;

@Data
public class FriendshipResponseDto extends PersonDto {
    private FriendshipCode friendshipStatus;
}
