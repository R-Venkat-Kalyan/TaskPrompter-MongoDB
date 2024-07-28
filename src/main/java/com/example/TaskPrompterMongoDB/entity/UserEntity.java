package com.example.TaskPrompterMongoDB.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Users")
public class UserEntity {

	private String userName;

	@Id
	private String userMail;

	private String password;

	private String rePassword;
	
	public UserEntity() {
		
	}
	
	public UserEntity(String userName, String userMail, String password, String rePassword) {
		super();
		this.userName = userName;
		this.userMail = userMail;
		this.password = password;
		this.rePassword = rePassword;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserMail() {
		return userMail;
	}

	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRePassword() {
		return rePassword;
	}

	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}
	

}
