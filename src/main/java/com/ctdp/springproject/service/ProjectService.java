package com.ctdp.springproject.service;

import com.ctdp.springproject.repository.ProjectRepository;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
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

    public Optional<Project> findProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAllProjects() {
        return (List<Project>) projectRepository.findAll();
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
