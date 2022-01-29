package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    Optional<List<Notification>> findByPersonId(Long id);
    Optional<List<Notification>> findByPersonIdAndWasSend(Long id, boolean wasSend);
    Optional<List<Notification>> findByPersonIdAndTypeId(Long personId, Long typeId);

}
