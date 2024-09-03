package com.example.TaskPrompterMongoDB.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Tasks")
public class ToDoEntity {

	@Id
	private String id;

	private String text;

	private LocalDate date;

	private LocalTime time;

	private String email;

	private String status;



	public ToDoEntity() {

	}

	public ToDoEntity(String text, LocalDate date, LocalTime time, String email, String status) {
		super();
		this.text = text;
		this.date = date;
		this.time = time;
		this.email = email;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;

	}
}
