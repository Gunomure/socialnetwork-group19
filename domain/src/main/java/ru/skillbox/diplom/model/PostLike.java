package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_like")
public class PostLike extends BaseEntity {
    private LocalDateTime time;
    @NotNull
    @Column(name = "person_id")
    private Long personId; //TODO change datatype to Person
    @NotNull
    @Column(name = "post_id")
    private Long postId; //TODO change datatype to Post
}