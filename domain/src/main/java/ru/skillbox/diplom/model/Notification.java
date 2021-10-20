package ru.skillbox.diplom.model;


import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "notification", schema = "group19")
public class Notification extends BaseEntity {

    @NotNull
    @Column(columnDefinition="type_id")
    private Long typeId;
    @NotNull
    @Column(columnDefinition="sent_time")
    private ZonedDateTime sentTime;
    @NotNull
    @Column(columnDefinition="person_id")
    private Long personId;
    @NotNull
    @Column(columnDefinition="entity_id")
    private Long entityId;
    //@NotNull TODO: Question: Does this column matter/has value in all situations?
    @Column(columnDefinition="entity_id")
    private String contact;
}
