package com.microservice.userssecurity.service;

import com.microservice.userssecurity.dto.AddressDto;
import com.microservice.userssecurity.dto.CreateUserDto;
import com.microservice.userssecurity.dto.ProductDto;
import com.microservice.userssecurity.model.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IUserService {
    public List<String> getUsernames();
    public Map<String, Object> createUser(CreateUserDto userDto);
    public void deleteUserEntity(int id);
    public CreateUserDto findUserEntity(int id);
    public String addProduct(int userId, int productCode);
    public String deleteProduct(int userId, int productCode);
    public List<ProductDto> getProducts();
    public List<ProductDto> getCartProducts(int userId);

    public Map<String, Object> addSellerRole(int userId);

    public void loginUser(String email, String password);

    public Optional<UserEntity> findByEmail(String email);

    public ResponseEntity<?> createProduct(String productName, int sellerCode,String sellerName, String brand, double price, String description, int amount, List<MultipartFile> images,String contentType) throws IOException;
}
