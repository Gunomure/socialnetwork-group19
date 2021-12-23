package ru.skillbox.diplom.model;

import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "countries")
public class Country extends BaseEntity {

    private String title;

    @OneToMany(mappedBy = "countryId", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<City> cities;
}
