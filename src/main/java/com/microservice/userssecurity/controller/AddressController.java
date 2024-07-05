package com.microservice.userssecurity.controller;

import com.microservice.userssecurity.exceptions.AddressNotFoundException;
import com.microservice.userssecurity.exceptions.UserNotFoundException;
import com.microservice.userssecurity.model.Address;
import com.microservice.userssecurity.service.AddressService;
import com.microservice.userssecurity.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/addresses")
public class AddressController {
    @Autowired
    IAddressService addressService;

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getAddressesFromUser(@PathVariable int userId) {
        try {
            Set<Address> addresses = addressService.getAddressesFromUser(userId);
            return ResponseEntity.ok(addresses);
        } catch (UserNotFoundException | AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/addAddress/{userId}")
    public ResponseEntity<?> addAddressToUser(@PathVariable int userId, @RequestBody Address address) {
        try {
            Address createdAddress = addressService.addAddressToUser(userId, address);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdAddress);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorResponse);
        }
    }
    @DeleteMapping("/delete/{userId}/{addressId}")
    public ResponseEntity<Map<String, Object>> removeAddressFromUser(@PathVariable int userId, @PathVariable int addressId) {
        try {
            Map<String, Object> response = addressService.removeAddressFromUser(userId, addressId);
            return ResponseEntity.ok(response);
        } catch (AddressNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error deleting address"));
        }
    }
}
