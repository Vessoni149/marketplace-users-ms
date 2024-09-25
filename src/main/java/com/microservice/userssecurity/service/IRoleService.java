package com.microservice.userssecurity.service;

import com.microservice.userssecurity.model.RoleEntity;

import java.util.Set;

public interface IRoleService {
    public Set<String> findAll();
}
