package com.microservice.userssecurity.dto;

import com.microservice.userssecurity.model.Address;
import com.microservice.userssecurity.model.RoleEntity;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String password;
    private List<ProductDto> products;

    private Set<Address> addresses;
    private Set<String> roles;

}
