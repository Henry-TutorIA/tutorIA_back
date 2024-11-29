package com.tutor_ia.back.repository;

import com.tutor_ia.back.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {

}
