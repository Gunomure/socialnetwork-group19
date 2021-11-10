package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_like")
public class PostLike extends BaseEntity {

    private ZonedDateTime time;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person personId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post postId;
}