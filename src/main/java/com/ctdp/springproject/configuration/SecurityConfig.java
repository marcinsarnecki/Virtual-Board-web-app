package com.ctdp.springproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests
                //.mvcMatchers("/").permitAll()
                .antMatchers("/admin").hasRole("admin")
                .antMatchers("/edit-descriptions").hasRole("admin")
                .antMatchers("/manage").hasRole("admin")
                .mvcMatchers("/register", "registration-confirmation", "/registration-failed").permitAll()
                .anyRequest().authenticated());
        http.formLogin(login -> login.loginPage("/login").defaultSuccessUrl("/", true).permitAll());
        http.csrf().disable();
        http.headers().frameOptions().disable();
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().mvcMatchers(
                "/css/**"
        );
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}