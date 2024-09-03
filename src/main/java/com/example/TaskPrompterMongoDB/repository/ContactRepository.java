package com.example.TaskPrompterMongoDB.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.TaskPrompterMongoDB.entity.ContactEntity;

public interface ContactRepository extends MongoRepository<ContactEntity, String> {

}
