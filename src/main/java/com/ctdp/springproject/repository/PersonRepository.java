package com.ctdp.springproject.repository;

import com.ctdp.springproject.model.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
}
