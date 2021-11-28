package ru.skillbox.diplom.model;

import lombok.*;
import org.hibernate.annotations.Type;
import ru.skillbox.diplom.model.enums.ReadStatus;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "message")
public class Message extends BaseEntity {
    @Column(name = "time")
    private ZonedDateTime time;
    @Column(name = "author_id")
    private Long authorId;
    @Column(name = "recipient_id")
    private Long recipientId;
    @Column(name = "message_text")
    private String messageText;
    @Column(name = "read_status", columnDefinition = "enum('SENT', 'READ')")
    @Enumerated(EnumType.STRING)
    private ReadStatus status;
    @Column(name = "is_deleted", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isDeleted;


}
