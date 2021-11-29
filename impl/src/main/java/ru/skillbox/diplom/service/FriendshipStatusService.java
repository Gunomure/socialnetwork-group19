package ru.skillbox.diplom.service;

import org.springframework.stereotype.Service;
import ru.skillbox.diplom.model.FriendshipStatus;
import ru.skillbox.diplom.model.enums.FriendshipCode;
import ru.skillbox.diplom.repository.FriendshipStatusRepository;

@Service
public class FriendshipStatusService {
    private FriendshipStatusRepository friendshipStatusRepository;

    public FriendshipStatusService(FriendshipStatusRepository friendshipStatusRepository) {
        this.friendshipStatusRepository = friendshipStatusRepository;
    }

    public FriendshipStatus getFriendshipStatus(FriendshipCode code) {
        return friendshipStatusRepository.findByCode(code);
    }
}
