package com.ctdp.springproject;

import com.ctdp.springproject.service.BadgeService;
import com.ctdp.springproject.service.BoardRecordService;
import com.ctdp.springproject.service.PersonService;
import com.ctdp.springproject.service.ProjectService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class SpringProjectApplication {

	public static void main(String[] args)  {
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
					} catch (IOException | URISyntaxException e) {
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
	static void addSampleData(PersonService personService, BoardRecordService boardRecordService, ProjectService projectService, BadgeService badgeService) {
		if(personService.findPersonByEmail("root").isEmpty())
			personService.add("root", "root", "root", "admin", "{bcrypt}$2a$12$feoV/JU6cAOReIxEOY.JP.Al4txSi50H60Y66qB96yuPW.55p6SKa");
	}
}