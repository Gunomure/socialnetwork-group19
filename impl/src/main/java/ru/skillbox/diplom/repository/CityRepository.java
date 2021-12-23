package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.diplom.model.City;
import ru.skillbox.diplom.model.Country;

import java.util.List;
import java.util.Optional;

import static org.hibernate.loader.Loader.SELECT;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByTitle(String name);
    List<City> getAllByCountryId(Country country);
}
