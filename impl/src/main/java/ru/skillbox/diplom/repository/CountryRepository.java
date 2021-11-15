package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.diplom.model.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByTitle(String name);

}
