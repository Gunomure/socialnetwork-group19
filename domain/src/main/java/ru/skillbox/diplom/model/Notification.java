package ru.skillbox.diplom.model;


import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
//TODO is it better to define schema in one place: application.yaml? In case it changes
@Table(name = "notification", schema = "group19")
public class Notification extends BaseEntity {

    @NotNull
    @Column(name="type_id")
    private Long typeId;
    @NotNull
    @Column(name="sent_time")
    private ZonedDateTime sentTime;
    @NotNull
    @Column(name="person_id")
    private Long personId;
    @NotNull
    @Column(name="entity_id")
    private Long entityId;
    //@NotNull TODO: Question: Does this column matter/has value in all situations?
    @Column(name="contact")
    private String contact;
}
