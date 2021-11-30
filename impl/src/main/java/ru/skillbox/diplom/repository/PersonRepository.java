package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.Person;


import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByEmail(String email);
    Optional<Person> findByConfirmationCode(String confirmationCode);
    Optional<Person> findById(Long id);

    @Query("SELECT COUNT(*) > 0 FROM Person p where p.email = :email")
    boolean isExists(String email);
}
