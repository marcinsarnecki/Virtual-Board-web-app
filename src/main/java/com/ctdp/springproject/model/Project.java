package com.ctdp.springproject.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "project")
    private List<BoardRecord> boardRecordList = new ArrayList<>();

    public void addBoardRecord(BoardRecord boardRecord) {
        boardRecordList.add(boardRecord);
    }

    public Project() {
    }

    public Project(String name) {
        this.name = name;
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

    public List<BoardRecord> getBoardRecordList() {
        return boardRecordList;
    }

    public void setBoardRecordList(List<BoardRecord> boardRecordList) {
        this.boardRecordList = boardRecordList;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
