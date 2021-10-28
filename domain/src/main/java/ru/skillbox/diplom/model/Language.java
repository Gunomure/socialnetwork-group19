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
@Table(name = "languages")
public class Language extends BaseEntity {
    @NotNull
    private String language;
}
