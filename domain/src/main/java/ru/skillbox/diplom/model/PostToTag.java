package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "post2tag")
public class PostToTag extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post postId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tagId;

}
