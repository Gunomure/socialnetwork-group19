package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity{

    private ZonedDateTime time;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Person authorId;

    private String title;

    @Column(name = "post_text")
    private String postText;

    @Column(name = "is_blocked")
    private Boolean isBlocked;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> comments;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostToTag> tags;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostLike> likes;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostFile> files;

}
