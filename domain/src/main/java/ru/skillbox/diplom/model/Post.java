package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostComment> comments;

    @ToString.Exclude
    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostLike> likes;

    @ToString.Exclude
    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL)
    private List<PostFile> files;

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostToTag> postToTags = new ArrayList<>();
}
