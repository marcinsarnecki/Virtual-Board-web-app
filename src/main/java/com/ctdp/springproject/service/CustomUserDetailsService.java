package com.ctdp.springproject.service;

import com.ctdp.springproject.model.Person;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PersonService personService;

    public CustomUserDetailsService(PersonService personService) {
        this.personService = personService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personService.findPersonByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", username)));
    }

    private UserDetails createUserDetails(Person person) {
        return User.builder()
                .username(person.getEmail())
                .password(person.getPassword())
                .roles(person.getRole())
                .build();
    }
}
