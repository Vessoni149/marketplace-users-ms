package com.microservice.userssecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

    @GetMapping("/accessAdmin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String accesAdmin(){
        return "hola, has accedido con el rol de ADMIN";
    }

    @GetMapping("/accessBuyer")
    @PreAuthorize("hasAuthority('USER_BUYER')")
    public String accesBuyer(){
        return "hola, has accedido con el rol de usuario comprador";
    }
    @GetMapping("/accessSeller")
    @PreAuthorize("hasAuthority('USER_SELLER')")
    public String accesSeller(){
        return "hola, has accedido con el rol de usuario vendedor";
    }
}

