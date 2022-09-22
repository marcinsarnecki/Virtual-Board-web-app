package com.ctdp.springproject;

import com.ctdp.springproject.dto.TableRecordDto;
import com.ctdp.springproject.model.*;
import com.ctdp.springproject.model.Color;
import com.ctdp.springproject.service.BadgeService;
import com.ctdp.springproject.service.BoardRecordService;
import com.ctdp.springproject.service.PersonService;
import com.ctdp.springproject.service.ProjectService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@SpringBootApplication
public class SpringProjectApplication {

	public static void main(String[] args) throws IOException {
		ConfigurableApplicationContext context = SpringApplication.run(SpringProjectApplication.class, args);
		PersonService personService = context.getBean(PersonService.class);
		BoardRecordService boardRecordService = context.getBean(BoardRecordService.class);
		ProjectService projectService = context.getBean(ProjectService.class);
		BadgeService badgeService = context.getBean(BadgeService.class);
		boardRecordService.setPersonService(personService);
		boardRecordService.setProjectService(projectService);
		addSampleData(personService, boardRecordService, projectService, badgeService);
		Timer timer = new Timer();//create tasks to reset boards every workday morning
		TimerTask[] timerTask = new TimerTask[5];
		for(int i = 0; i < 5; i++)
			timerTask[i] = new TimerTask() {
			@Override
			public void run() {
				try {
					projectService.clearAndSave();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		Calendar currentDate = Calendar.getInstance();
		Calendar[] days = new Calendar[5];
		for(int i = 0; i < 5; i++) {
			days[i] = Calendar.getInstance();
			days[i].set(Calendar.DAY_OF_WEEK, i + 2);
			days[i].set(Calendar.HOUR, 8);
			days[i].set(Calendar.MINUTE, 0);
			days[i].set(Calendar.SECOND, 0);
			days[i].set(Calendar.AM_PM, 0);
			if(currentDate.compareTo(days[i]) > 0)
				days[i].add(Calendar.DAY_OF_MONTH, 7);
		}
		for(int i = 0; i < 5; i++)
			timer.schedule(timerTask[i], days[i].getTime(), 1000 * 60 * 60 * 24 * 7);
	}
	@Bean
	public PasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	static void addSampleData(PersonService personService, BoardRecordService boardRecordService, ProjectService projectService, BadgeService badgeService) {
		// sample data, random names, passwords 12345
		Person person1 = personService.add("Jan", "Kowalski", "jk@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person2 = personService.add("Adam", "Nowak", "an@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person3 = personService.add("Przemek", "Jankowski", "pj@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person4 = personService.add("Dominik", "Pawlak", "dp@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person5 = personService.add("Piotr", "Zawadzki", "pz@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");

		Person person6 = personService.add("abc", "def", "ad@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");


		Project project1 = projectService.add("telefony");
		Project project2 = projectService.add("laptopy");
		Project project3 = projectService.add("5g");


		boardRecordService.addPersonToProject(1L, 1L);
		boardRecordService.addPersonToProject(1L, 2L);
		boardRecordService.addPersonToProject(2L, 1L);
		boardRecordService.addPersonToProject(3L, 2L);
		boardRecordService.addPersonToProject(4L, 2L);
		boardRecordService.addPersonToProject(5L, 3L);
		boardRecordService.addPersonToProject(2L, 2L);
		boardRecordService.addPersonToProject(2L, 3L);
		boardRecordService.addPersonToProject(4L, 3L);
		boardRecordService.addPersonToProject(5L, 2L);

		boardRecordService.addPersonToProject(6L, 2L);

		boardRecordService.addBadge(1L, 2L, new Badge(Color.ORANGE));
		boardRecordService.addBadge(1L, 2L, new Badge(Color.GREEN));
		boardRecordService.addBadge(1L, 2L, new Badge(Color.RED));
		boardRecordService.addBadge(1L, 2L, new Badge(Color.GREEN));
		boardRecordService.addBadge(2L, 1L, new Badge(Color.GREEN));
		boardRecordService.addBadge(1L, 1L, new Badge(Color.GREEN));
		boardRecordService.removeBadge(1L, 2L, new Badge(Color.ORANGE));
		boardRecordService.addBadge(3L, 2L, new Badge(Color.RED));

		personService.add("Marcin", "Sarnecki", "marcin.sarnecki44@gmail.com", "admin", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
	}
}
