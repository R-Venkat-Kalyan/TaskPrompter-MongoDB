package com.example.TaskPrompterMongoDB.repository;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.example.TaskPrompterMongoDB.entity.ToDoEntity;

public interface ToDoRepository extends MongoRepository<ToDoEntity, String> {

}
