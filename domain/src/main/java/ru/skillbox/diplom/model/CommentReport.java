package ru.skillbox.diplom.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Data
@Table(name = "comment_reports")
public class CommentReport extends BaseEntity{

    private ZonedDateTime time;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private PostComment comment;
}
