package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity{

    private String tag;

    @OneToMany(mappedBy = "tagId")
    private List<PostToTag> postToTag;

}
