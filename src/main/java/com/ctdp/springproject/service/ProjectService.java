package com.ctdp.springproject.service;

import com.ctdp.springproject.dto.TableRecordDto;
import com.ctdp.springproject.repository.ProjectRepository;
import com.ctdp.springproject.model.BoardRecord;
import com.ctdp.springproject.model.Person;
import com.ctdp.springproject.model.Project;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final BadgeService badgeService;

    public ProjectService(ProjectRepository projectRepository, BadgeService badgeService) {
        this.projectRepository = projectRepository;
        this.badgeService = badgeService;
    }
    private String fileName(String projectName) {
        return projectName + " " + new SimpleDateFormat("yyyy-MM-dd hh-mm-ss'.png'").format(new Date());
    }
    @Transactional
    public Project add(String name) {
        Project project = new Project(name);
        projectRepository.save(project);
        return project;
    }
    @Transactional
    public void deleteProjectById(Long id) {
        projectRepository.deleteById(id);
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

    @Transactional
    public void clearAndSave() throws IOException {//create images of boards and delete all badges
        System.out.println("JESTEM TUTAJ!!!!!!!!");
        List<Project> projectList = this.findAllProjects();
        File backgroundTemplate = new File("src/main/resources/static/background-template.png");
        File red = new File("src/main/resources/static/red.png");
        File green = new File("src/main/resources/static/green.png");
        File blue = new File("src/main/resources/static/blue.png");
        File yellow = new File("src/main/resources/static/yellow.png");
        File orange = new File("src/main/resources/static/orange.png");
        File transparent = new File("src/main/resources/static/transparent.png");
        BufferedImage redImage = ImageIO.read(red);
        BufferedImage greenImage = ImageIO.read(green);
        BufferedImage blueImage = ImageIO.read(blue);
        BufferedImage yellowImage = ImageIO.read(yellow);
        BufferedImage orangeImage = ImageIO.read(orange);
        BufferedImage transparentImage = ImageIO.read(transparent);
        BufferedImage backgroundImage = ImageIO.read(backgroundTemplate);
        Font font = new Font("Arial", Font.PLAIN, 20);
        if(projectList.isEmpty())
            return;
        for(Project project: projectList) {
            String projectName = project.getName();
            List<TableRecordDto> tableRecordDtoList = this.findTableRecordDtoListByProjectName(projectName);
            int len = tableRecordDtoList.size();
            int height = 50 + len * 75 + 50;
            int imageHeight = 250;
            while(imageHeight < height)
                imageHeight += 250;
            BufferedImage tableImage = new BufferedImage(1092, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tableImage.createGraphics();
            g.setFont(font);
            g.setColor(Color.BLACK);
            for(int y = 0; y < imageHeight; y += 250)
                g.drawImage(backgroundImage, 0, y, null);
            int currentY = 25;
            for(TableRecordDto tableRecordDto: tableRecordDtoList) {
                int currentX = 25;
                g.drawString(tableRecordDto.name, currentX, currentY + 45);
                currentX += 275;
                for(String badge: tableRecordDto.badges) {
                    switch (badge) {
                        case "red.png" -> g.drawImage(redImage, currentX, currentY, null);
                        case "green.png" -> g.drawImage(greenImage, currentX, currentY, null);
                        case "blue.png" -> g.drawImage(blueImage, currentX, currentY, null);
                        case "orange.png" -> g.drawImage(orangeImage, currentX, currentY, null);
                        case "yellow.png" -> g.drawImage(yellowImage, currentX, currentY, null);
                        default -> g.drawImage(transparentImage, currentX, currentY, null);}
                    currentX += 75;
                }
                currentY += 75;
                currentX = 25;
            }
            g.dispose();
            File file = new File("src/main/resources/static/boards/" + fileName(projectName));
            ImageIO.write(tableImage, "png", file);
        }
        badgeService.deleteAll();
    }
}
