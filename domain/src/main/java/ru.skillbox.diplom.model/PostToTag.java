package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "post2tag")
public class PostToTag extends BaseEntity {
    @NotNull
    @Column(name = "post_id")
    private Long postId;
    @NotNull
    @Column(name = "tag_id")
    private Long tagId;
}
