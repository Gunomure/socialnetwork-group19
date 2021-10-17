package ru.skillbox.diplom.model;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.diplom.model.Enum.MessagePermission;

import java.util.Date;

@Getter @Setter
public class Person extends BaseEntity{
    private String firstName;
    private String lastName;
    private Date registrationDate;
    private Date birthDate;
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String description;
    private String city;
    private String country;
    private String confirmationCode;
    private Boolean isApproved;
    private MessagePermission permission;
    private Date lastOnlineTime;
    private Boolean isBlocked;
}
