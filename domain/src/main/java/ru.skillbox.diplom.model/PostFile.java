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
@Table(name = "post_file")
public class PostFile extends BaseEntity {
    @NotNull
    @Column(name = "post_id")
    private Long postId; //TODO change datatype to Post
    @NotNull
    private String name;
    @NotNull
    private String path;
}
