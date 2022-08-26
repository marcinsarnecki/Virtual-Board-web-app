package com.ctdp.springproject;

import com.ctdp.springproject.dto.TableRecordDto;
import com.ctdp.springproject.model.*;
import com.ctdp.springproject.service.BadgeService;
import com.ctdp.springproject.service.BoardRecordService;
import com.ctdp.springproject.service.PersonService;
import com.ctdp.springproject.service.ProjectService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class SpringProjectApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringProjectApplication.class, args);
		PersonService personService = context.getBean(PersonService.class);
		BoardRecordService boardRecordService = context.getBean(BoardRecordService.class);
		ProjectService projectService = context.getBean(ProjectService.class);
		BadgeService badgeService = context.getBean(BadgeService.class);
		boardRecordService.setPersonService(personService);
		boardRecordService.setProjectService(projectService);
		addSampleData(personService, boardRecordService, projectService, badgeService);

		Scanner sc = new Scanner(System.in);
		while(true) { //simple console to control database
			System.out.println();
			System.out.println("-1 exit");
			System.out.println("1 add Person <name> <surname> <email> <role> <password hash>");
			System.out.println("2 add Project <name>");
			System.out.println("3 add PersonToProject <personId> <projectId>");
			System.out.println("4 add Badge <personId> <projectId> <color>");
			System.out.println("5 remove Badge <personId> <projectId> <color>");
			System.out.println("6 find AllPersons");
			System.out.println("7 find AllProjects");
			System.out.println("8 find AllProjectsByPersonId <personId>");
			System.out.println("9 find AllPersonsByProjectId <projectId>");
			System.out.println("10 find AllBadgesByPersonAndProjectId <personId> <projectId>");
			System.out.println("11 find BoardRecordsByProjectName <projectName>");
			System.out.println();
			int input = sc.nextInt();
			if(input == -1)
				break;
			if(input == 1) {
				String name, surname, email, role, password;
				name = sc.next();
				surname = sc.next();
				email = sc.next();
				role = sc.next();
				password = sc.next();
				personService.add(name, surname, email, role, password);
			}
			if(input == 2) {
				String name;
				name = sc.next();
				projectService.add(name);
			}
			if(input == 3) {
				long personId, projectId;
				personId = sc.nextLong();
				projectId = sc.nextLong();
				boardRecordService.addPersonToProject(personId, projectId);
			}
			if(input == 4) {
				long personId, projectId;
				int color;
				personId = sc.nextLong();
				projectId = sc.nextLong();
				color = sc.nextInt();
				boardRecordService.addBadge(personId, projectId, new Badge(Color.values()[color]));
			}
			if(input == 5) {
				long personId, projectId;
				int color;
				personId = sc.nextLong();
				projectId = sc.nextLong();
				color = sc.nextInt();
				boardRecordService.removeBadge(personId, projectId, new Badge(Color.values()[color]));
			}
			if(input == 6) {
				List<Person> allPersons = personService.findAllPersons();
				for(Person person: allPersons)
					System.out.println(person);
			}
			if(input == 7) {
				List<Project> allProjects = projectService.findAllProjects();
				for(Project project: allProjects)
					System.out.println(project);
			}
			if(input == 8) {
				long id = sc.nextLong();
				List<Project> projectList = personService.findAllProjectsByPersonId(id);
				for(Project project: projectList)
					System.out.println(project);
			}
			if(input == 9) {
				long id = sc.nextLong();
				List<Person> personList = projectService.findAllPersonsByProjectId(id);
				for(Person person: personList)
					System.out.println(person);
			}
			if(input == 10) {
				long personId, projectId;
				personId = sc.nextLong();
				projectId = sc.nextLong();
				List<Badge> badgeList = boardRecordService.findALlBadgesByPersonAndProjectId(personId, projectId);
				for(Badge badge: badgeList)
					System.out.println(badge.toString());
			}
			if(input == 11) {
				String name;
				name = sc.next();
				List<BoardRecord> boardRecordList = projectService.findBoardRecordsByProjectName(name);
				for(BoardRecord boardRecord: boardRecordList) {
					System.out.println(boardRecord.toString());
				}
			}
		}
	}
	static void addSampleData(PersonService personService, BoardRecordService boardRecordService, ProjectService projectService, BadgeService badgeService) {
		// sample data, random names, passwords 12345
		Person person1 = personService.add("Jan", "Kowalski", "jk@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person2 = personService.add("Adam", "Nowak", "an@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person3 = personService.add("Przemek", "Jankowski", "pj@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person4 = personService.add("Dominik", "Pawlak", "dp@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
		Person person5 = personService.add("Piotr", "Zawadzki", "pz@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");

		Person person6 = personService.add("e", "e", "ee@example.com","consultant", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");


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
		boardRecordService.removeBadge(1L, 2L, new Badge(Color.ORANGE));
		boardRecordService.addBadge(3L, 2L, new Badge(Color.RED));

		personService.add("Marcin", "Sarnecki", "marcin.sarnecki44@gmail.com", "admin", "{bcrypt}$2a$12$9QkS/O.ewR2VQfM8fGFGTOoUyzEFQbHsBEXQZW6fg2Z1L/ebRL2by");
	}

}
