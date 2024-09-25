package com.microservice.userssecurity.model;


import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String fullName;

    @NotBlank
    private String password;

    @ElementCollection
    private List<Integer> products;

    @OneToMany(fetch = FetchType.EAGER, targetEntity = Address.class, cascade= CascadeType.PERSIST)
    @JoinTable(name = "user_addresses" ,  joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="address_id"))
    private Set<Address> addresses;

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade= CascadeType.PERSIST)
    @JoinTable(name = "user_roles" ,  joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="role_id"))
    @Enumerated(EnumType.STRING)
    private Set<RoleEntity> roles;


}
