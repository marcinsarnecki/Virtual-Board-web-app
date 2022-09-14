package com.ctdp.springproject.model;

import javax.persistence.*;

@Entity
public class PersonRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public PersonRecord(Person person) {
        this.person = person;
    }

    public PersonRecord() {
    }
}
