package ru.skillbox.diplom.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tags")
public class Tag extends BaseEntity{

    @Column(name = "tag_name")
    private String tagName;

    public Tag(String tagName){
        this.tagName = tagName;
    }

    @OneToMany(mappedBy = "tag")
    private List<PostToTag> postToTag;

}
