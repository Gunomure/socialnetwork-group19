package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "comment_like")
public class CommentLike extends BaseEntity {

    private ZonedDateTime time;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person personId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private PostComment commentId;
}
