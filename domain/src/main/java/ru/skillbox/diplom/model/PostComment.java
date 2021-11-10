package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_comments")
public class PostComment extends BaseEntity {

    private ZonedDateTime time;

    @NotNull
    @ManyToOne
    @JoinColumn(name="post_id", insertable = false, updatable = false)
    private Post postId;

    @OneToOne
    @JoinColumn(name="parent_id")
    private PostComment parentId;

    @NotNull
    @ManyToOne
    @JoinColumn(name="author_id", insertable = false, updatable = false)
    private Person authorId;

    @NotNull
    @ManyToOne
    @JoinColumn(name="post_id", insertable = false, updatable = false)
    private Post post;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "is_blocked")
    private Boolean isBlocked;
}

