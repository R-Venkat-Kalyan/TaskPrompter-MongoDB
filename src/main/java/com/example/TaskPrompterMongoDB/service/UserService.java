package com.example.TaskPrompterMongoDB.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.TaskPrompterMongoDB.entity.UserEntity;
import com.example.TaskPrompterMongoDB.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository ur;
	
	public void registerUser(UserEntity ue) {
		ur.save(ue);
	}
	
	public List<UserEntity> getAllUsers(){
		return ur.findAll();
	}
	
	public void deleteByMail(String mail) {
		ur.deleteById(mail);
	}
	
	public UserEntity getByEmail(String email) {
		return ur.findById(email).orElse(null);
		
	}

}
