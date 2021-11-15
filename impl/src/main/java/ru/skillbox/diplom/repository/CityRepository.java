package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.diplom.model.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByTitle(String name);
}
