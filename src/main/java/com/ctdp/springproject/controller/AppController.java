package com.ctdp.springproject.controller;

import com.ctdp.springproject.dto.ChangePasswordDto;
import com.ctdp.springproject.dto.PersonDto;
import com.ctdp.springproject.dto.PersonRegistrationDto;
import com.ctdp.springproject.dto.TableRecordDto;
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
            for(Project project: personService.findAllProjectsByPersonEmail(username))
                projectList.add(project.getName());
            List<List<TableRecordDto>> lists = new ArrayList<>();
            for (String project : projectList)
                lists.add(projectService.findTableRecordDtoListByProjectName(project));
            model.addAttribute("size", projectList.size());
            model.addAttribute("tables", lists);
            model.addAttribute("projects", projectList);
        }
        if(Objects.equals(role, "admin")) {
            List<PersonRecord> personRecordList = personService.findAllPersonRecordsByAdminEmail(username);
            HashSet<Long> hashSet = new HashSet<Long>();
            for(PersonRecord personRecord: personRecordList)
                hashSet.add(personRecord.getPointer_id());
            List<BoardRecord> boardRecordList = boardRecordService.findAllBoardRecords();
            HashMap<String, List<TableRecordDto>> hashMap = new HashMap<String, List<TableRecordDto>>();
            for(BoardRecord boardRecord: boardRecordList) {
                String project = boardRecord.getProject().getName();
                Long person_id = boardRecord.getPerson().getId();
                if(!hashSet.contains(person_id))
                    continue;
                if(!hashMap.containsKey(project))
                    hashMap.put(project, new ArrayList<TableRecordDto>());
                hashMap.get(project).add(new TableRecordDto(boardRecord));
            }
            List<List<TableRecordDto>> lists = new ArrayList<>(hashMap.values());
            model.addAttribute("size", hashMap.keySet().size());
            model.addAttribute("tables", lists);
            model.addAttribute("projects", hashMap.keySet().toArray());
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
}
