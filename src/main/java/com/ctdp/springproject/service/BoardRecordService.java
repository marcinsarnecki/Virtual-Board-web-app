package com.ctdp.springproject.service;

import com.ctdp.springproject.repository.BoardRecordRepository;
import com.ctdp.springproject.model.Badge;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardRecordService {
    private final BoardRecordRepository boardRecordRepository;
    private PersonService personService;
    private ProjectService projectService;
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    public BoardRecordService(BoardRecordRepository boardRecordRepository) {
        this.boardRecordRepository = boardRecordRepository;
    }

    @Transactional
    public BoardRecord addPersonToProject(Long personId, Long projectId) {
        try {
            Person person = personService.findPersonById(personId).orElseThrow();
            Project project = projectService.findProjectById(projectId).orElseThrow();
            BoardRecord boardRecord = new BoardRecord(person, project);
            person.addBoardRecord(boardRecord);
            project.addBoardRecord(boardRecord);
            boardRecordRepository.save(boardRecord);
            return boardRecord;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Transactional
    public void addBadge(Long personId, Long projectId, Badge badge) {
        try {
            BoardRecord boardRecord = boardRecordRepository.findBoardRecordByPersonIdAndProjectId(personId, projectId).orElseThrow();
            boardRecord.addBadge(badge);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public void removeBadge(Long personId, Long projectId, Badge badge) {
        try {
            BoardRecord boardRecord = boardRecordRepository.findBoardRecordByPersonIdAndProjectId(personId, projectId).orElseThrow();
            boardRecord.removeBadge(badge);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Transactional
    public List<Badge> findALlBadgesByPersonAndProjectId(Long personId, Long projectId) {
        try {
            BoardRecord boardRecord = boardRecordRepository.findBoardRecordByPersonIdAndProjectId(personId, projectId).orElseThrow();
            Hibernate.initialize(boardRecord.getBadgeList());
            return boardRecord.getBadgeList();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
