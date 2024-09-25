package com.microservice.userssecurity.repository;

import com.microservice.userssecurity.model.ERole;
import com.microservice.userssecurity.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository  extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByName(ERole name);
}
