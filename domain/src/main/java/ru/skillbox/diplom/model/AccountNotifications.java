package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "account_notifications")
public class AccountNotifications extends BaseEntity {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Person authorId;

    @Column(name = "POST_COMMENT", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean postComment;

    @Column(name = "COMMENT_COMMENT", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean commentComment;

    @Column(name = "FRIEND_REQUEST", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean friendRequest;

    @Column(name = "MESSAGE", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean message;

    @Column(name = "FRIEND_BIRTHDAY", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean friendBirthday;

    @Column(name = "NEW_POST", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean newPost;

    @Column(name = "SEND_EMAIL_MESSAGE", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("false")
    private Boolean sendEmailMessage;

    @Column(name = "SEND_PHONE_MESSAGE", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("false")
    private Boolean sendPhoneMessage;
}
