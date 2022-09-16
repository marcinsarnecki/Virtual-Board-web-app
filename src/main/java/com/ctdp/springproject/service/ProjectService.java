package com.ctdp.springproject.service;

import com.ctdp.springproject.dto.TableRecordDto;
import com.ctdp.springproject.repository.ProjectRepository;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project add(String name) {
        Project project = new Project(name);
        projectRepository.save(project);
        return project;
    }
    @Transactional
    public void setDescriptions(String name, String red, String green, String blue, String yellow, String orange) {
        try {
            Project project = projectRepository.findByName(name).orElseThrow();
            project.setRedBadgeDescription(red);
            project.setGreenBadgeDescription(green);
            project.setBlueBadgeDescription(blue);
            project.setYellowBadgeDescription(yellow);
            project.setOrangeBadgeDescription(orange);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Optional<Project> findProjectById(Long id) {
        return projectRepository.findById(id);
    }
    public Optional<Project> findProjectByName(String name) { return projectRepository.findByName(name);}

    public List<Project> findAllProjects() {
        return (List<Project>) projectRepository.findAll();
    }

    @Transactional
    public List<BoardRecord> findBoardRecordsByProjectName(String name) {
        try {
            Project project = projectRepository.findByName(name).orElseThrow();
            Hibernate.initialize(project.getBoardRecordList());
            return project.getBoardRecordList();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Transactional
    public List<TableRecordDto> findTableRecordDtoListByProjectName(String name) {
        List<TableRecordDto> tableRecordDtoList = new ArrayList<>();
        List<BoardRecord> boardRecordList = this.findBoardRecordsByProjectName(name);
        if(boardRecordList == null)
            return null;
        for(BoardRecord boardRecord : boardRecordList)
            tableRecordDtoList.add(new TableRecordDto(boardRecord));
        return tableRecordDtoList;
    }

    @Transactional
    public List<Person> findAllPersonsByProjectId(Long id) {
        try{
            Project project = projectRepository.findById(id).orElseThrow();
            List<Person> personList = new ArrayList<>();
            for(BoardRecord boardRecord: project.getBoardRecordList())
                personList.add(boardRecord.getPerson());
            return personList;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }
}
