package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.AccountNotifications;
import ru.skillbox.diplom.model.Person;

@Repository
public interface AccountNotificationRepository extends JpaRepository<AccountNotifications, Long>, JpaSpecificationExecutor<AccountNotifications> {

    AccountNotifications findByAuthorId(Person authorId);
}
