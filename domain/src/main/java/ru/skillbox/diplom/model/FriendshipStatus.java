package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.enums.FriendshipCode;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "friendship_status")
public class FriendshipStatus extends BaseEntity {
    @Column(name = "time")
    private ZonedDateTime time;
    @Column(name = "name")
    private String name;
    @Column(columnDefinition = "enum('REQUEST', 'FRIEND', 'BLOCKED', 'DECLINED', 'SUBSCRIBED')")
    @Enumerated(EnumType.STRING)
    private FriendshipCode code;
}


