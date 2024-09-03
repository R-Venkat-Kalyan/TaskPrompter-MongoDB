package com.example.TaskPrompterMongoDB.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.TaskPrompterMongoDB.entity.ContactEntity;
import com.example.TaskPrompterMongoDB.repository.ContactRepository;


@Service
public class ContactService {
	
	@Autowired
	private ContactRepository cr;
	
	public void saveContact(ContactEntity ce) {
		cr.save(ce);
	}

}
