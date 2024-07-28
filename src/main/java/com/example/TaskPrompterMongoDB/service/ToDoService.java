package com.example.TaskPrompterMongoDB.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.TaskPrompterMongoDB.entity.ToDoEntity;
import com.example.TaskPrompterMongoDB.repository.ToDoRepository;
import com.example.TaskPrompterMongoDB.repository.UserRepository;

@Service
public class ToDoService {
	
	@Autowired
	private ToDoRepository tdr;
	
	@Autowired
	private UserRepository ur;
	
	public void saveTask(ToDoEntity te) {
		if (te.getStatus() == null || te.getStatus().isEmpty()) {
            te.setStatus("Not Notified");
        }
		tdr.save(te);
	}
	
	public List<ToDoEntity> allTasks(){
		return tdr.findAll();
		}
	
	public ToDoEntity getByTask(String id) {
		return tdr.findById(id).orElse(null);
	}
	
	public void deleteTask(String id) {
		tdr.deleteById(id);
	}

}
