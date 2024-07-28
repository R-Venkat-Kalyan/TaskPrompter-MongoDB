package com.example.TaskPrompterMongoDB.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TaskPrompterMongoDB.entity.UserEntity;

public interface UserRepository extends MongoRepository<UserEntity, String> {

}
