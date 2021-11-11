package ru.skillbox.diplom.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersSearchRequest {
    // TODO bind fields from RequestParam to camel case
    private String first_name;
    private String last_name;
    private int age_from = 0;
    private int age_to = 200;
    private String country;
    private String city;
    private int offset = 0;
    private int itemPerPage = 10;
}
