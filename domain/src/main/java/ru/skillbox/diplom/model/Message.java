package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.enums.ReadStatus;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends BaseEntity{
    @Column(name = "time")
    private ZonedDateTime time;
    @Column(name = "author_id")
    private Long authorId;
    @Column(name = "recipient_id")
    private Long recipientId;
    @Column(name = "message_text")
    private String messageText;
    @Column(name = "read_status")
    @Enumerated(EnumType.STRING)
    private ReadStatus status;
}
