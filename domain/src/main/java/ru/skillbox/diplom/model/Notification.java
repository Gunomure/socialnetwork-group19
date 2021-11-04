package ru.skillbox.diplom.model;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity {

    @NotNull
    @Column(name = "type_id")
    private Long typeId;
    @NotNull
    @Column(name = "sent_time")
    private ZonedDateTime sentTime;
    @NotNull
    @Column(name = "person_id")
    private Long personId;
    @NotNull
    @Column(name = "entity_id")
    private Long entityId;
    //@NotNull TODO: Question: Does this column matter/has value in all situations?
    @Column(name = "contact")
    private String contact;
}
