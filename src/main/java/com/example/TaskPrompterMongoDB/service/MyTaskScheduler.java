package com.example.TaskPrompterMongoDB.service;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.TaskPrompterMongoDB.entity.ToDoEntity;

@Component
public class MyTaskScheduler {

	@Autowired
	private ToDoService toDoService;

	@Autowired
	private JavaMailSender mailSender;
	
	private static final ZoneId INDIAN_ZONE = ZoneId.of("Asia/Kolkata");

	@Scheduled(fixedRate = 120000) // 2 minutes
	public void processTasks() {
		// Retrieve tasks from the database
		List<ToDoEntity> tasks = toDoService.allTasks();
		// Process tasks

		for (ToDoEntity task : tasks) {
			System.out.println(task.getEmail() + " " + task.getStatus() + " " + task.getTime() + " " + task.getText()
					+ " " + task.getDate());
			if (isTaskDue(task)) {
				// Send email
				sendEmail(task);

			}
		}
	}

//	MongoDB Time issue fix
	private boolean isTaskDue(ToDoEntity task) {

		LocalDate date = task.getDate();
		LocalTime time = task.getTime();
		//System.out.println("actual: " + date + " " + time);
		//LocalDateTime now = LocalDateTime.now();
		//LocalDateTime taskDateTime = LocalDateTime.of(task.getDate(), task.getTime());
		//System.out.println("updated " + taskDateTime);
//		taskDateTime = taskDateTime.minusHours(5).minusMinutes(30);
		LocalDateTime now = LocalDateTime.now(INDIAN_ZONE);
        LocalDateTime taskDateTime = LocalDateTime.of(date, time).atZone(INDIAN_ZONE).toLocalDateTime();


		if ("Notified".equals(task.getStatus())) {
			return false;
		}
		// Check if the task's date is today and the time is within the next hour
		return taskDateTime.toLocalDate().isEqual(now.toLocalDate()) && taskDateTime.isBefore(now.plusHours(1));
	}

	private void sendEmail(ToDoEntity task) {
		String from = "taskprompter@gmail.com";
		String to = task.getEmail();

		// Prepare the HTML email content
		MimeMessagePreparator preparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject("ToDo Alert!!");

			// Read HTML email template
			Resource resource = new ClassPathResource("templates/AlertTemplate.html");
			String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));

			// Adjust the taskDateTime by subtracting 6 hours and 30 minutes
//			LocalTime adjustedTaskDateTime = task.getTime().minusHours(6).minusMinutes(30);

			// Format the adjusted taskDateTime
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
//			String adjustedFormattedTime = adjustedTaskDateTime.format(formatter);
//			String adjustedFormattedTime = task.getTime().format(formatter);
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(INDIAN_ZONE);
            String formattedTime = task.getTime().format(formatter);

			// Replace placeholders with task details including the adjusted formatted time
            String emailBody = emailTemplate.replace("{{taskText}}", task.getText())
                    .replace("{{taskTime}}", formattedTime);

			// Set the HTML content
			helper.setText(emailBody, true);
		};

		// Send the email
		mailSender.send(preparator);

		// Update task status
		task.setStatus("Notified");
		toDoService.saveTask(task);

		System.out.println("Notification email has been sent");
	}

}
