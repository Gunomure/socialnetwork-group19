package ru.skillbox.diplom.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "tags")
public class Tag extends BaseEntity{

    private String tag;

    public Tag(String tag){
        this.tag = tag;
    }

    @OneToMany(mappedBy = "tagId")
    private List<PostToTag> postToTag;

}
