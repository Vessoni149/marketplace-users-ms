package com.microservice.userssecurity.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private int code;
    private String productName;
    private String sellerName;
    private int sellerCode;
    private int amount;
    private String brand;
    private double price;
    private String description;
    private Set<ImageDto> image = new HashSet<>();
}
