package com.microservice.userssecurity.service;


import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws
            UsernameNotFoundException {

        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        if (!userEntityOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        } else {
            UserEntity userEntity = userEntityOptional.get();
            Collection<? extends GrantedAuthority> authorities = userEntity.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())))
                    .collect(Collectors.toSet());

            return new User(userEntity.getEmail(), userEntity.getPassword(),
                    true, true, true, true, authorities);
        }
    }
}

