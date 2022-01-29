package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.enums.NotificationTypes;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "notification_type")
public class NotificationType extends BaseEntity {
    @NotNull
    private String code;
    @NotNull
    @Column(columnDefinition = "enum('POST', 'POST_COMMENT', 'COMMENT_COMMENT', 'FRIEND_REQUEST', 'MESSAGE', 'BIRTHDAY')")
    @Enumerated(EnumType.STRING)
    private NotificationTypes name;
}
