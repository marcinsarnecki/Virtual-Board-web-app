package com.ctdp.springproject.model;

import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String role;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(unique = true, nullable = false)
    private String email;
    @OneToMany(mappedBy = "person")//, fetch = FetchType.EAGER, cascade = CascadeType.ALL
    private List<BoardRecord> boardRecordList = new ArrayList<>();

    public void addBoardRecord(BoardRecord boardRecord) {
        boardRecordList.add(boardRecord);
    }

    public List<BoardRecord> getBoardRecordList() {
        return boardRecordList;
    }

    public void setBoardRecordList(List<BoardRecord> boardRecordList) {
        this.boardRecordList = boardRecordList;
    }

    public Person() {
    }

    public Person(String name, String surname, String email, String role, String password) {
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.password = password;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", role='" + role + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
