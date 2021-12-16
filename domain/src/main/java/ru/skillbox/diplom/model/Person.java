package ru.skillbox.diplom.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.enums.UserType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "persons")
@Data
public class
Person extends User {

    @Column(name = "registration_date", nullable = false)
    private ZonedDateTime registrationDate;

    @Column(columnDefinition = "VARCHAR(16)")
    private String phone;

    private String photo;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_approved", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("true")
    private Boolean isApproved;

    @Column(name = "message_permission", nullable = false, columnDefinition = "enum('ALL', 'FRIEND')")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("ALL")
    private MessagePermission permission;

    @Column(name = "last_online_time")
    private ZonedDateTime lastOnlineTime;

    @Column(name = "is_blocked", columnDefinition = "BOOLEAN NOT NULL")
    @ColumnDefault("false")
    private Boolean isBlocked;

    @OneToMany(mappedBy = "authorId", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Post> posts;

    // join Friendship by srcPerson field
    @OneToMany(
            mappedBy = "srcPerson",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<Friendship> friendsLeft;

    // join Friendship by dstPerson field
    @OneToMany(
            mappedBy = "dstPerson",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<Friendship> friendsRight;

    @Override
    @Transient
    public UserType getType() {
        return super.getType();
    }
}

