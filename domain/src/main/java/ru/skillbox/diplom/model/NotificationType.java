package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
//TODO is it better to define schema in one place: application.yaml? In case it changes
@Table(name = "notification_type", schema = "group19")
public class NotificationType extends BaseEntity {
    @NotNull
    private String code;
    @NotNull
    private String name;
}
