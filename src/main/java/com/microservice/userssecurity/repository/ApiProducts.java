package com.microservice.userssecurity.repository;


import com.microservice.userssecurity.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@FeignClient(name="ms-products", url="localhost:8084")
public interface ApiProducts {
    @GetMapping("/products/get/{code}")
    public ProductDto getProduct(@PathVariable int code);
    @GetMapping("/products/get")
    public List<ProductDto> getProducts();


    //Es necesario enviar con feing los parametros anotados con @RequestPart en vez de con @requestParam para que no de error al enviar las imagenes.
    @PostMapping(value = "/products/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ProductDto> createProduct(@RequestPart("productName") String productName,
                                             @RequestPart("sellerCode") int sellerCode,
                                             @RequestPart("sellerName") String sellerName,
                                             @RequestPart("brand") String brand,
                                             @RequestPart("price") double price,
                                             @RequestPart("description") String description,
                                             @RequestPart("amount") int amount,
                                             @RequestPart("images") List<MultipartFile> images,
                                             @RequestHeader(HttpHeaders.CONTENT_TYPE) String contentType);


}
