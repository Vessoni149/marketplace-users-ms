package com.microservice.userssecurity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    @NotBlank
    private int postalCode;
    @NotBlank
    private String state;
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    private int streetNumber;

    private String apartment;

    private String betweenStreet1;
    private String betweenStreet2;
    @NotBlank
    private Boolean workOrResidential;
    @NotBlank
    private int contactNumber;
    private String additionalInstructions;
}
