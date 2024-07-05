package com.microservice.userssecurity.service;

import com.microservice.userssecurity.dto.AddressDto;
import com.microservice.userssecurity.dto.CreateUserDto;
import com.microservice.userssecurity.dto.ProductDto;
import com.microservice.userssecurity.model.Address;
import com.microservice.userssecurity.model.ERole;
import com.microservice.userssecurity.model.RoleEntity;
import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.ApiProducts;
import com.microservice.userssecurity.repository.IAddressRepository;
import com.microservice.userssecurity.repository.IRoleRepository;
import com.microservice.userssecurity.repository.IUserRepository;
import com.microservice.userssecurity.securityConfig.Jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class UserService implements IUserService{
    @Autowired
    private IUserRepository userRepo;
    @Autowired
    private Validations validations;
    @Autowired
    private ApiProducts produApi;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    IAddressRepository addressRepository;

    @Override
    public List<String> getUsernames() {
        List<UserEntity> usersList = userRepo.findAll();
        List<String> usernamesList = new ArrayList<>();
        for (UserEntity user : usersList) {
            usernamesList.add(user.getFullName());
        }
        return usernamesList;
    }





    @Override
    public Map<String, Object> createUser(CreateUserDto userDto) {
        //validar email:
        String email = userDto.getEmail();
        String emailValidationResult = validations.validateEmail(email);
        if (emailValidationResult != null) {
            // Si la validación del correo electrónico falla, devuelve el mensaje de error.
            Map<String, Object> response = new HashMap<>();
            response.put("message", emailValidationResult);
            return response;
        }

        //Validar la contraseña:
        String password = userDto.getPassword();
        if (!validations.validatePassword(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid password. Please make sure it contains at least 6 characters, one number, one special character, and one capital letter.");
            return response;
        }

        //validar nombre:
        String name = userDto.getFullName();
        if (!validations.validateName(name)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "The username must be between 3 and 30 characters.");
            return response;
        }


        //creando lista vacía:
        List<Integer> productsList = new ArrayList<>();


        // Buscar el rol USER_BUYER en la base de datos
        RoleEntity userBuyerRole = roleRepository.findByName(ERole.USER_BUYER)
                .orElseThrow(() -> new RuntimeException("Error: Role USER_BUYER not found."));

        // Crear un conjunto con el rol USER_BUYER
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(userBuyerRole);
        //Crear Set con direcciones vacío.
        Set<Address> adresses = new HashSet<>();

        //creando userEntity que será persistida:
        UserEntity userEntity = UserEntity.builder()
                .fullName(userDto.getFullName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail())
                .roles(roles)
                .products(productsList)
                .addresses(adresses)
                .build();

        UserEntity savedUser = userRepo.save(userEntity);

        // Generar el token JWT
        String token = jwtUtils.generateAccessToken(savedUser);

        // Crear un objeto Map con el token JWT y otros datos
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "User created successfully");
        return response;
    }



    @Override
    public Map<String, Object> addSellerRole(int userId) {
        // Buscar el usuario por su ID
        Optional<UserEntity> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();

            // Buscar el rol SELLER en la base de datos
            RoleEntity sellerRole = roleRepository.findByName(ERole.USER_SELLER)
                    .orElseThrow(() -> new RuntimeException("Error: Role SELLER not found."));

            // Agregar el rol SELLER al conjunto de roles del usuario
            Set<RoleEntity> roles = user.getRoles();
            roles.add(sellerRole);
            user.setRoles(roles);

            // Guardar los cambios en la base de datos
            userRepo.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Seller role added successfully");
            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User not found");
            return response;
        }
    }

    @Override
    public void loginUser(String email, String password) {
        // Buscar al usuario por email en la base de datos
        Optional<UserEntity> userEntityOptional = userRepo.findByEmail(email);

        // Verificar si el usuario existe
        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();

            // Verificar si la contraseña ingresada coincide con la contraseña encriptada del usuario
            if (passwordEncoder.matches(password, userEntity.getPassword())) {
                // Si las credenciales son válidas, generar el token JWT
                String token = jwtUtils.generateAccessToken(userEntity);
            }
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void deleteUserEntity(int id) {
        userRepo.deleteById(id);
    }

    @Override
    public CreateUserDto findUserEntity(int id) {
// Buscar el usuario en la base de datos
        Optional<UserEntity> optionalUser = userRepo.findById(id);

        // Verificar si el usuario existe
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            CreateUserDto userDto = new CreateUserDto();
            userDto.setFullName(user.getFullName());
            userDto.setEmail(user.getEmail());
            userDto.setPassword(user.getPassword());

            List<ProductDto> productsList = new ArrayList<>();
            List<Integer> productIdList = user.getProducts();

            // Verificar si el usuario tiene productos asociados
            if (productIdList != null) {
                for (Integer productId: productIdList) {
                    productsList.add(produApi.getProduct(productId));
                }
            }

            userDto.setProducts(productsList);
            return userDto;
        } else {
            // Si no se encuentra el usuario, devolver un objeto CreateUserDto vacío o null según tu necesidad
            return null;
        }
    }

    //Products:

    @Override
    public List<ProductDto> getProducts() {

        return produApi.getProducts();
    }

    @Override
    public List<ProductDto> getCartProducts(int userId){
        UserEntity user = userRepo.findById(userId).orElse(null);
        List<Integer>productIds = user.getProducts();
        List<ProductDto> userCart = new ArrayList<>();
        for(Integer productId : productIds){
            ProductDto product = produApi.getProduct(productId);
            userCart.add(product);
        }
        return userCart;
    }
    @Override
    public String addProduct(int userId, int productCode){
        UserEntity user = userRepo.findById(userId).orElse(null);

        ProductDto produDto = produApi.getProduct(productCode);
        List<Integer> productsList = user.getProducts();
        productsList.add(produDto.getCode());
        user.setProducts(productsList);
        userRepo.save(user);

        List<ProductDto> productDtoList = new ArrayList<>();
        productDtoList.add(produDto);
        CreateUserDto userDto = new CreateUserDto();
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setProducts(productDtoList);


        return "Product added to list";
    }

    @Override
    public String deleteProduct(int userId, int productCode) {
        UserEntity user = userRepo.findById(userId).orElse(null);

        List<Integer> productsList = user.getProducts();
        productsList.remove(Integer.valueOf(productCode));
        user.setProducts(productsList);

        userRepo.save(user);

        return "Product deleted";
    }

    public ResponseEntity<?> createProduct(String productName, int sellerCode,String sellerName, String brand,
                                           double price, String description, int amount,
                                           List<MultipartFile> images, String contentType) throws IOException {

        return produApi.createProduct(productName, sellerCode, sellerName, brand, price, description, amount, images, contentType);
    }


}
