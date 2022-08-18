package com.ctdp.springproject.controller;

import com.ctdp.springproject.dto.PersonRegistrationDto;
import com.ctdp.springproject.service.PersonService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

@Controller
public class AppController {
    private final PersonService personService;

    public AppController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/login")
    String loginPage() {
        return "login-form";
    }
    @GetMapping("/")
    String home(Model model) {
        return "index";
    }
    @GetMapping("/register")
    String registrationPage(Model model)
    {
        PersonRegistrationDto personRegistrationDto = new PersonRegistrationDto();
        model.addAttribute("user", personRegistrationDto);
        return "registration-form";
    }
    @PostMapping("/register")
    String register(PersonRegistrationDto personRegistrationDto) {
        if(!Objects.equals(personRegistrationDto.getPassword(), personRegistrationDto.getConfirmedPassword())) {
            System.out.println("Wprowadzono inne hasła przy próbie rejestracji!");
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
