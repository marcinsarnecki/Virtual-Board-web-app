package com.ctdp.springproject.controller;

import com.ctdp.springproject.dto.*;
import com.ctdp.springproject.model.*;
import com.ctdp.springproject.service.BoardRecordService;
import com.ctdp.springproject.service.PersonService;
import com.ctdp.springproject.service.ProjectService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class AppController {
    private final PersonService personService;
    private final ProjectService projectService;
    private final BoardRecordService boardRecordService;
    private final PasswordEncoder passwordEncoder;

    public AppController(PersonService personService, ProjectService projectService, BoardRecordService boardRecordService, PasswordEncoder passwordEncoder) {
        this.personService = personService;
        this.projectService = projectService;
        this.boardRecordService = boardRecordService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    String loginPage() {
        return "login-form";
    }
    @GetMapping("/")
    String home(Model model, HttpSession session) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = personService.findPersonByEmail(username).orElseThrow().getRole();
        if(Objects.equals(role, "consultant")) {
            List<String> projectList = new ArrayList<>();
            List<String[]> descriptions = new ArrayList<>();
            for(Project project: personService.findAllProjectsByPersonEmail(username)) {
                projectList.add(project.getName());
                descriptions.add(project.getDescriptions());
            }
            List<List<TableRecordDto>> lists = new ArrayList<>();
            for (String project : projectList)
                lists.add(projectService.findTableRecordDtoListByProjectName(project));
            model.addAttribute("size", projectList.size());
            model.addAttribute("tables", lists);
            model.addAttribute("projects", projectList);
            model.addAttribute("descriptions", descriptions);
        }
        if(Objects.equals(role, "admin")) {
            List<PersonRecord> personRecordList = personService.findAllPersonRecordsByAdminEmail(username);
            HashSet<Long> personRecordIdHashSet = new HashSet<Long>();
            for(PersonRecord personRecord: personRecordList)
                personRecordIdHashSet.add(personRecord.getPointer_id());
            List<BoardRecord> boardRecordList = boardRecordService.findAllBoardRecords();
            HashMap<String, List<TableRecordDto>> projectToTableRecordDtoList = new HashMap<>();
            HashMap<String, String[]> projectToDescription = new HashMap<>();
            for(BoardRecord boardRecord: boardRecordList) {
                Project project = boardRecord.getProject();
                Long person_id = boardRecord.getPerson().getId();
                if(!personRecordIdHashSet.contains(person_id))
                    continue;
                if(!projectToTableRecordDtoList.containsKey(project.getName()))
                    projectToTableRecordDtoList.put(project.getName(), new ArrayList<TableRecordDto>());
                projectToTableRecordDtoList.get(project.getName()).add(new TableRecordDto(boardRecord));
                projectToDescription.put(project.getName(), project.getDescriptions());
            }
            List<String> projects = new ArrayList<>();
            List<List<TableRecordDto>> lists = new ArrayList<>();
            List<String[]> descriptions = new ArrayList<>();
            for(Map.Entry<String, List<TableRecordDto>> mapEntry: projectToTableRecordDtoList.entrySet()) {
                String project = mapEntry.getKey();
                projects.add(project);
                lists.add(mapEntry.getValue());
                descriptions.add(projectToDescription.get(project));
            }
            model.addAttribute("size", projectToTableRecordDtoList.size());
            model.addAttribute("tables", lists);
            model.addAttribute("projects", projects);
            model.addAttribute("descriptions", descriptions);
        }
        return "index";
    }
    @GetMapping("/add")
    String add(Model model) {
        List<String> projectList = new ArrayList<>();
        for(Project project: projectService.findAllProjects())
            projectList.add(project.getName());
        model.addAttribute("projectList", projectList);
        return "add";
    }
    @Transactional
    @PostMapping("/add-badge")
    String addBadge(Model model, @RequestParam(defaultValue = "no-color") String color, @RequestParam(defaultValue = "no-project") String project) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!Objects.equals(color, "no-color") && !Objects.equals(project, "no-project"))
            boardRecordService.addBadge(username, project, new Badge(
                    switch (color) {
                        case "blue" -> Color.BLUE;
                        case "green" -> Color.GREEN;
                        case "orange" -> Color.ORANGE;
                        case "yellow" -> Color.YELLOW;
                        default -> Color.RED;}));
        model.addAttribute("projectList", personService.findAllProjectsByPersonEmail(username));
        model.addAttribute("mainTable", projectService.findTableRecordDtoListByProjectName(project));
        return "redirect:/";
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
    @GetMapping("/change-password")
    String changePassword(Model model) {
        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        model.addAttribute("changePasswordDto", changePasswordDto);
        return "change-password-form";
    }
    @Transactional
    @PostMapping("/change-password")
    String changePasswd(ChangePasswordDto changePasswordDto, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personService.findPersonByEmail(username).orElseThrow();
        if(!passwordEncoder.matches(changePasswordDto.getOldPassword(), person.getPassword())) {
            return UriComponentsBuilder
                    .fromPath("redirect:change-password-result")
                    .queryParam("code", 1)
                    .build().toString();
        }
        else if(!Objects.equals(changePasswordDto.getNewPassword(), changePasswordDto.getNewConfirmedPassword())){
            return UriComponentsBuilder
                    .fromPath("redirect:change-password-result")
                    .queryParam("code", 2)
                    .build().toString();
        }
        else if(Objects.equals(changePasswordDto.getNewPassword(), changePasswordDto.getOldPassword())) {
            return UriComponentsBuilder
                    .fromPath("redirect:change-password-result")
                    .queryParam("code", 3)
                    .build().toString();
        }
        else {
            personService.changePassword(username, passwordEncoder.encode(changePasswordDto.getNewConfirmedPassword()));
            return UriComponentsBuilder
                    .fromPath("redirect:change-password-result")
                    .queryParam("code", 0)
                    .build().toString();
        }
    }
    @GetMapping("/change-password-result")
    String changePasswdResult(@RequestParam Long code, Model model) {
        if(code == 0)
            model.addAttribute("message", "Pomyślnie zmieniono hasło!");
        if(code == 1)
            model.addAttribute("message", "Nieprawidłowe obecne hasło!");
        if(code == 2)
            model.addAttribute("message", "Podane nowe hasła różnią się!");
        if(code == 3)
            model.addAttribute("message", "Nowe hasło nie różni się od obecnego!");
        return "change-password-result";
    }
    @GetMapping("/admin")
    String adminPage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<PersonRecord> personRecordList = personService.findAllPersonRecordsByAdminEmail(username);
        HashSet<Long> hashSet = new HashSet<Long>();
        for(PersonRecord personRecord: personRecordList)
            hashSet.add(personRecord.getPointer_id());
        List<Person> personList = personService.findAllPersons();
        List<PersonDto> personDtoList = new ArrayList<>();
        for(Person person: personList)
            if(Objects.equals(person.getRole(), "consultant")) {
                PersonDto personDto = new PersonDto();
                personDto.setEmail(person.getEmail());
                personDto.setName(person.getName());
                personDto.setSurname(person.getSurname());
                personDto.setId(person.getId());
                if(hashSet.contains(person.getId()))
                    personDto.setChecked(true);
                personDtoList.add(personDto);
            }
        personDtoList.sort((p1, p2) -> p1.getSurname().compareTo(p2.getSurname()));//sorting by surname alphabetically
        model.addAttribute("list", personDtoList);
        return "admin";
    }
    @PostMapping("/users")
    String users(@RequestParam List<Long> values) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        personService.setNewPersonRecordsByAdminEmail(username, values);
        return "redirect:/";
    }

    @GetMapping("/confirmation")
    String registrationConfirmation() {
        return "registration-confirmation";
    }

    @GetMapping("/edit-descriptions")
    String editDescriptionsPage(Model model) {
        List<Project> projectList = projectService.findAllProjects();
        model.addAttribute("projects", projectList);
        return "edit-descriptions";
    }
    @Transactional
    @PostMapping("/edit-descriptions")
    String postEditDescriptions(Model model,
                                @RequestParam List<String> projects,
                                @RequestParam List<String> redDescriptions,
                                @RequestParam List<String> blueDescriptions,
                                @RequestParam List<String> greenDescriptions,
                                @RequestParam List<String> yellowDescriptions,
                                @RequestParam List<String> orangeDescriptions) {
        for(int i = 0; i < projects.size(); i++)
            projectService.setDescriptions(projects.get(i),
                    redDescriptions.get(i),
                    blueDescriptions.get(i),
                    greenDescriptions.get(i),
                    yellowDescriptions.get(i),
                    orangeDescriptions.get(i));
        return "redirect:/";
    }
    @GetMapping("/manage")
    String managePage(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Project> projectList = projectService.findAllProjects();
        model.addAttribute("projectList", projectList);
        List<Person> personList = personService.findAllPersons();
        List<PersonDto> personDtoList = new ArrayList<>();
        List<PersonDto> adminDtoList = new ArrayList<>();
        for(Person person: personList){
            PersonDto personDto = new PersonDto();
            personDto.setEmail(person.getEmail());
            personDto.setName(person.getName());
            personDto.setSurname(person.getSurname());
            personDto.setId(person.getId());
            if(Objects.equals(person.getRole(), "consultant"))
                personDtoList.add(personDto);
            else if(!Objects.equals(person.getEmail(), username))
                adminDtoList.add(personDto);
        }
        personDtoList.sort((p1, p2) -> p1.getSurname().compareTo(p2.getSurname()));//sorting by surname alphabetically
        adminDtoList.sort((p1, p2) -> p1.getSurname().compareTo(p2.getSurname()));
        model.addAttribute("personList", personDtoList);
        model.addAttribute("adminList", adminDtoList);
        return "manage";
    }
    @Transactional
    @PostMapping("/delete-person")
    String deletePerson(Model model, @RequestParam(defaultValue = "-1") Long deletedPersonId) {
        if(deletedPersonId != -1) {
            personService.deletePersonById(deletedPersonId);
        }
        return "redirect:/";
    }
    @Transactional
    @PostMapping("/delete-project")
    String deleteProject(Model model, @RequestParam(defaultValue = "-1") Long deletedProjectId) {
        System.out.println("deleted id = " + deletedProjectId);
        if(deletedProjectId != -1) {
            projectService.deleteProjectById(deletedProjectId);
        }
        return "redirect:/";
    }
    @Transactional
    @PostMapping("/add-project")
    String deleteProject(Model model, @RequestParam(defaultValue = "no-project") String newProjectName) {
        if(!Objects.equals(newProjectName, "no-project")) {
            projectService.add(newProjectName);
        }
        return "redirect:/";
    }
    @Transactional
    @PostMapping("promote-person")
    String promotePerson(Model model, @RequestParam(defaultValue = "-1") Long personId) {
        if(personId != -1) {
            personService.promoteToAdmin(personId);
        }
        return "redirect:/";
    }
}
