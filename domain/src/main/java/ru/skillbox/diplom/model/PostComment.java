package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "post_comments")
public class PostComment extends BaseEntity {

    private ZonedDateTime time;

    @NotNull
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @OneToOne
    @JoinColumn(name="parent_id")
    private PostComment parent;

    @NotNull
    @ManyToOne
    @JoinColumn(name="author_id")
    private Person author;

    @Column(name = "comment_text")
    private String commentText;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @ToString.Exclude
    @OneToMany(mappedBy = "commentId", cascade = CascadeType.ALL)
    private List<CommentLike> likes;

}

