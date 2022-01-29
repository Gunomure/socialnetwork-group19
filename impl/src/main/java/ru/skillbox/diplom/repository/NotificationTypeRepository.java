package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.NotificationType;
import ru.skillbox.diplom.model.enums.NotificationTypes;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long>, JpaSpecificationExecutor<NotificationType> {
    NotificationType findByName(NotificationTypes name);
}
