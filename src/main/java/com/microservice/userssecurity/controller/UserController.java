package com.microservice.userssecurity.controller;

import com.microservice.userssecurity.dto.AddressDto;
import com.microservice.userssecurity.dto.CreateUserDto;
import com.microservice.userssecurity.dto.LoginDto;
import com.microservice.userssecurity.dto.ProductDto;
import com.microservice.userssecurity.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private IUserService userServ;



    @GetMapping("/get/all")
    public List<String> getUsers(){
        return userServ.getUsernames();
    }

    @GetMapping("/get/{id}")
    public CreateUserDto getUser(@PathVariable int id){
        return userServ.findUserEntity(id);
    }

    @PostMapping("/createUserEntity")
    public ResponseEntity<Map<String, Object>> createUserEntity(@RequestBody CreateUserDto userDto) {
        Map<String, Object> response = userServ.createUser(userDto);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public void loginUser(@RequestBody LoginDto loginDto) {
        userServ.loginUser(loginDto.getEmail(), loginDto.getPassword());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id){
        userServ.deleteUserEntity(id);
        return "User deleted";
    }



    //Products:
    @GetMapping("/products/get")
    public List<ProductDto> getProducts(){return userServ.getProducts();}

    @GetMapping("/products/get/{userId}")
    public List<ProductDto> getCartProducts(@PathVariable int userId){
        return userServ.getCartProducts(userId);
    }

    @PutMapping("/products/add/{userId}/{productCode}")
    public String addProduct(@PathVariable int userId, @PathVariable int productCode){
        return userServ.addProduct(userId,productCode);
    }
    @PutMapping("/products/delete/{userId}/{productCode}")
    public String deleteProduct(@PathVariable int userId, @PathVariable int productCode) {
        return userServ.deleteProduct(userId, productCode);
    }

    @PutMapping("/addSellerRole/{userId}")
    public Map<String, Object> addSellerRole(@PathVariable int userId){
        return userServ.addSellerRole(userId);
    }

    @PostMapping(value = "/products/create", consumes = { "multipart/form-data" })
    public ResponseEntity<?> saveProduct(@RequestParam("productName") String productName,
                                         @RequestParam("sellerCode") int sellerCode,
                                         @RequestParam("sellerName") String sellerName,
                                         @RequestParam("brand") String brand,
                                         @RequestParam("price") double price,
                                         @RequestParam("description") String description,
                                         @RequestParam("amount") int amount,
                                         @RequestParam("images") List<MultipartFile> images,
                                         @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType
                                         ) throws IOException {
        try {
            userServ.createProduct(productName, sellerCode,sellerName, brand, price, description, amount, images, contentType);
            return ResponseEntity.ok("Product saved successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product");
        }
    }

}
