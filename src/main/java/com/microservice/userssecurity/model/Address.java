package com.microservice.userssecurity.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int id;
    @NotNull
    private int postalCode;
    @NotBlank
    private String state;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotNull
    private int streetNumber;

    private String apartment;

    private String betweenStreet1;
    private String betweenStreet2;
    @NotNull
    private Boolean workOrResidential;
    @NotNull
    private Long contactNumber;
    private String additionalInstructions;

}
