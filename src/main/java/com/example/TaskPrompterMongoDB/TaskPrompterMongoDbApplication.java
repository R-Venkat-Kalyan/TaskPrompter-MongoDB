package com.example.TaskPrompterMongoDB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class TaskPrompterMongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskPrompterMongoDbApplication.class, args);
	}

}
