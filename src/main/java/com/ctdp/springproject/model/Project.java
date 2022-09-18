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
    private String redBadgeDescription = "opis";
    private String greenBadgeDescription = "opis";
    private String blueBadgeDescription = "opis";
    private String yellowBadgeDescription = "opis";
    private String orangeBadgeDescription = "opis";
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

    public String getRedBadgeDescription() {
        return redBadgeDescription;
    }

    public void setRedBadgeDescription(String redBadgeDescription) {
        this.redBadgeDescription = redBadgeDescription;
    }

    public String getGreenBadgeDescription() {
        return greenBadgeDescription;
    }

    public void setGreenBadgeDescription(String greenBadgeDescription) {
        this.greenBadgeDescription = greenBadgeDescription;
    }

    public String getBlueBadgeDescription() {
        return blueBadgeDescription;
    }

    public void setBlueBadgeDescription(String blueBadgeDescription) {
        this.blueBadgeDescription = blueBadgeDescription;
    }

    public String getYellowBadgeDescription() {
        return yellowBadgeDescription;
    }

    public void setYellowBadgeDescription(String yellowBadgeDescription) {
        this.yellowBadgeDescription = yellowBadgeDescription;
    }

    public String getOrangeBadgeDescription() {
        return orangeBadgeDescription;
    }

    public void setOrangeBadgeDescription(String orangeBadgeDescription) {
        this.orangeBadgeDescription = orangeBadgeDescription;
    }
    public String[] getDescriptions() {
        return new String[]{redBadgeDescription, greenBadgeDescription, blueBadgeDescription, yellowBadgeDescription, orangeBadgeDescription};
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", redBadgeDescription='" + redBadgeDescription + '\'' +
                ", greenBadgeDescription='" + greenBadgeDescription + '\'' +
                ", blueBadgeDescription='" + blueBadgeDescription + '\'' +
                ", yellowBadgeDescription='" + yellowBadgeDescription + '\'' +
                ", orangeBadgeDescription='" + orangeBadgeDescription + '\'' +
                '}';
    }
}
