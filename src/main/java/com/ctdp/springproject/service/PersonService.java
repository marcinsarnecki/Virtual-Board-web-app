package com.ctdp.springproject.service;

import com.ctdp.springproject.dto.PersonRegistrationDto;
import com.ctdp.springproject.repository.PersonRepository;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Person add(String name, String surname, String email, String role, String password) {
        Person person = new Person(name, surname, email, role, password);
        personRepository.save(person);
        return person;
    }

    @Transactional
    public void register(PersonRegistrationDto personRegistrationDto) {
        Person person = new Person();
        person.setName(personRegistrationDto.getName());
        person.setSurname(personRegistrationDto.getSurname());
        person.setEmail(personRegistrationDto.getEmail());
        person.setPassword(passwordEncoder.encode(personRegistrationDto.getPassword()));
        person.setRole("consultant");//domyslnie z poziomu rejestracji nowa osoba jest konsultantem, nie adminem
        personRepository.save(person);
    }
    @Transactional
    public List<Project> findAllProjectsByPersonId(Long id) {
        try {
            Person person = personRepository.findById(id).orElseThrow();
            List<Project> projectList = new ArrayList<>();
            for(BoardRecord boardRecord: person.getBoardRecordList())
                projectList.add(boardRecord.getProject());
            return projectList;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<Person> findAllPersons() {
        return (List<Person>) personRepository.findAll();
    }
    public Optional<Person> findPersonById(Long id) { return personRepository.findById(id); }

    public Optional<Person> findPersonByEmail(String email) {
        return personRepository.findByEmail(email); }
}
