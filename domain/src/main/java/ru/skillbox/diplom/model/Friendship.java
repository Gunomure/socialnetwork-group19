package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "friendship")
public class Friendship extends BaseEntity {
    @NotNull
    @OneToOne(optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="status_id")
    private FriendshipStatus statusId;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "src_person_id")
    private Person srcPerson;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "dst_person_id")
    private Person dstPerson;
}
