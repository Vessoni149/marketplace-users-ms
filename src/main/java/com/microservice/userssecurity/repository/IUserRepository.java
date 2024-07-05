package com.microservice.userssecurity.repository;

import com.microservice.userssecurity.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<UserEntity, Integer> {
     public Optional<UserEntity> findByEmail(String email);
}
