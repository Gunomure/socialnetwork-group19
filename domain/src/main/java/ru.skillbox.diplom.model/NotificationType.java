package ru.skillbox.diplom.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "notification_type", schema = "group19")
public class NotificationType extends BaseEntity {
    @NotNull
    private String code;
    @NotNull
    private String name;
}
