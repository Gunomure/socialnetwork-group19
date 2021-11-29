package ru.skillbox.diplom.model;

import lombok.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "friendship_status")
// It's a dictionary of statuses. One row for ona status
public class FriendshipStatus extends BaseEntity {
    @Column(name = "time")
    private ZonedDateTime time;
    @Column(name = "name")
    private String name;
    @Column(columnDefinition = "enum('REQUEST', 'FRIEND', 'BLOCKED', 'DECLINED', 'SUBSCRIBED')")
    @Enumerated(EnumType.STRING)
    private FriendshipCode code;
}