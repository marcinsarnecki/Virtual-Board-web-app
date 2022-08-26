package com.ctdp.springproject.controller;

import com.ctdp.springproject.dto.PersonRegistrationDto;
import com.ctdp.springproject.model.Badge;
import com.ctdp.springproject.model.Color;
import com.ctdp.springproject.model.Project;
import com.ctdp.springproject.service.BoardRecordService;
import com.ctdp.springproject.service.PersonService;
import com.ctdp.springproject.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@Controller
public class AppController {
    private final PersonService personService;
    private final ProjectService projectService;
    private final BoardRecordService boardRecordService;

    public AppController(PersonService personService, ProjectService projectService, BoardRecordService boardRecordService) {
        this.personService = personService;
        this.projectService = projectService;
        this.boardRecordService = boardRecordService;
    }

    @GetMapping("/login")
    String loginPage() {
        return "login-form";
    }
    @GetMapping("/")
    String home(Model model, HttpSession session, @RequestParam(name = "project", defaultValue = "no-project-passed") String project) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        session.setAttribute("username", username);
        if(Objects.equals(project, "no-project-passed")) { //domyslnie pierwszy na liscie projekt uzytkownika
            List<Project> projectList = personService.findAllProjectsByPersonEmail(username);
            if(projectList.size() != 0)
                project = projectList.get(0).getName();
        }
        model.addAttribute("project", project);
        model.addAttribute("projectList", personService.findAllProjectsByPersonEmail(username));
        model.addAttribute("mainTable", projectService.findTableRecordDtoListByProjectName(project));
        return "index";
    }
    @Transactional
    @PostMapping("/add-badge")
    String addBadge(Model model, @RequestParam String color, @RequestParam String project) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boardRecordService.addBadge(username, project, new Badge(
                switch (color) {
                    case "blue" -> Color.BLUE;
                    case "green" -> Color.GREEN;
                    case "orange" -> Color.ORANGE;
                    case "yellow" -> Color.YELLOW;
                    default -> Color.RED;}));
        model.addAttribute("project", project);
        model.addAttribute("projectList", personService.findAllProjectsByPersonEmail(username));
        model.addAttribute("mainTable", projectService.findTableRecordDtoListByProjectName(project));
        return UriComponentsBuilder
                .fromPath("redirect:")
                .queryParam("project", project)
                .build().toString();
    }
    @Transactional
    @PostMapping("/remove-last-badge")
    String removeLastBadge(Model model, @RequestParam String project) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boardRecordService.removeLastBadge(username, project);
        model.addAttribute("project", project);
        model.addAttribute("projectList", personService.findAllProjectsByPersonEmail(username));
        model.addAttribute("mainTable", projectService.findTableRecordDtoListByProjectName(project));
        return UriComponentsBuilder
                .fromPath("redirect:")
                .queryParam("project", project)
                .build().toString();
    }
    @GetMapping("/register")
    String registrationPage(Model model) {
        PersonRegistrationDto personRegistrationDto = new PersonRegistrationDto();
        model.addAttribute("user", personRegistrationDto);
        return "registration-form";
    }
    @PostMapping("/register")
    String register(PersonRegistrationDto personRegistrationDto, Model model) {
        if(personService.findPersonByEmail(personRegistrationDto.getEmail()).isPresent()) {
            String message = "Podany adres email już istnieje!";
            System.out.println(message);
            model.addAttribute("message", message);
            return "registration-failed";
        }
        if(!Objects.equals(personRegistrationDto.getPassword(), personRegistrationDto.getConfirmedPassword())) {
            String message = "Wprowadzono inne hasła przy próbie rejestracji!";
            System.out.println(message);
            model.addAttribute("message", message);
            return "registration-failed";
        }
        personService.register(personRegistrationDto);
        return "redirect:/confirmation";
    }
    @GetMapping("/confirmation")
    String registrationConfirmation() {
        return "registration-confirmation";
    }
}
