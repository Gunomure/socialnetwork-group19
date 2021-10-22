package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.enums.Action;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "blocks_history")
public class BlockHistory extends BaseEntity{

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Column(name = "person_id", columnDefinition = "BIGINT NOT NULL")
    private Long personId;

    @Column(name = "post_id", columnDefinition = "BIGINT")
    private Long postId;

    @Column(name = "comment_id", columnDefinition = "BIGINT")
    private Long commentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;
}
