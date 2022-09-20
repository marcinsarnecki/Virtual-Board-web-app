package com.ctdp.springproject.service;

import com.ctdp.springproject.dto.ChangePasswordDto;
import com.ctdp.springproject.dto.PersonRegistrationDto;
import com.ctdp.springproject.model.PersonRecord;
import com.ctdp.springproject.repository.PersonRecordRepository;
import com.ctdp.springproject.repository.PersonRepository;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PersonRecordRepository personRecordRepository;

    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder, PersonRecordRepository personRecordRepository) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.personRecordRepository = personRecordRepository;
    }

    @Transactional
    public Person add(String name, String surname, String email, String role, String password) {
        Person person = new Person(name, surname, email, role, password);
        personRepository.save(person);
        return person;
    }
    @Transactional
    public void changePassword(String email, String password) {
        Person person = personRepository.findByEmail(email).orElseThrow();
        person.setPassword(password);
    }

    @Transactional
    public void addPersonRecord(String adminEmail, Long pointer_id) {
        Person admin = personRepository.findByEmail(adminEmail).orElseThrow();
        PersonRecord personRecord = new PersonRecord(pointer_id);
        admin.addPersonRecord(personRecord);
        personRecordRepository.save(personRecord);
    }

    @Transactional
    public List<PersonRecord> findAllPersonRecordsByAdminEmail(String email) {
        Person admin = personRepository.findByEmail(email).orElseThrow();
        Hibernate.initialize(admin.getPersonRecordList());
        return admin.getPersonRecordList();
    }
    @Transactional
    public void setNewPersonRecordsByAdminEmail(String email, List<Long> list) {
        Person admin = personRepository.findByEmail(email).orElseThrow();
        admin.getPersonRecordList().clear();
        for(Long id: list)
            admin.addPersonRecord(new PersonRecord(id));
    }
    @Transactional
    public void promoteToAdmin(Long personId) {
        Person person = personRepository.findById(personId).orElseThrow();
        person.getBoardRecordList().clear();
        person.setRole("admin");
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
    @Transactional
    public List<Project> findAllProjectsByPersonEmail(String email) {
        try {
            Person person = personRepository.findByEmail(email).orElseThrow();
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
    @Transactional
    public void deletePersonById(Long id) {
        personRepository.deleteById(id);
    }
    public List<Person> findAllPersons() {
        return (List<Person>) personRepository.findAll();
    }
    public Optional<Person> findPersonById(Long id) { return personRepository.findById(id); }

    public Optional<Person> findPersonByEmail(String email) {
        return personRepository.findByEmail(email); }
}
