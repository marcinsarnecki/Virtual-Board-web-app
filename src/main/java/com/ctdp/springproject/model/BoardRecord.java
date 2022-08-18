package com.ctdp.springproject.model;

import org.hibernate.annotations.Cascade;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class BoardRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_record_id")
    private List<Badge> badgeList = new ArrayList<>();

    public void addBadge(Badge badge) { badgeList.add(badge); }
    public void removeBadge(Badge badge) {
        int index = 0;
        for(Badge it: badgeList) {
            if(it.getColor() == badge.getColor() &&
                    Objects.equals(it.getDescription(), badge.getDescription()) &&
                    Objects.equals(it.getUrl(), badge.getUrl())) {
                badgeList.remove(index);
                return;
            }
            index++;
        }
    }

    public BoardRecord() {
    }

    public BoardRecord(Person person, Project project) {
        this.person = person;
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Badge> getBadgeList() {
        return badgeList;
    }

    public void setBadgeList(List<Badge> badgeList) {
        this.badgeList = badgeList;
    }

    @Override
    public String toString() {
        return "BoardRecord{" +
                "id=" + id +
                ", person=" + person +
                ", project=" + project +
                '}';
    }
}
