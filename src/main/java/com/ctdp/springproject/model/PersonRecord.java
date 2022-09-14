package com.ctdp.springproject.model;

import javax.persistence.*;

@Entity
public class PersonRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pointer_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPointer_id() {
        return pointer_id;
    }

    public void setPointer_id(Long pointer_id) {
        this.pointer_id = pointer_id;
    }

    public PersonRecord(Long pointer_id) {
        this.pointer_id = pointer_id;
    }

    public PersonRecord() {
    }
}
