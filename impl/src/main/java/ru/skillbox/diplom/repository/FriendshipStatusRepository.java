package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.FriendshipStatus;
import ru.skillbox.diplom.model.enums.FriendshipCode;

@Repository
public interface FriendshipStatusRepository extends JpaRepository<FriendshipStatus, Integer>, JpaSpecificationExecutor<FriendshipStatus> {

    FriendshipStatus findByCode(FriendshipCode code);
}
