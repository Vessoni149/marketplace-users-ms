package com.microservice.userssecurity.service;

import com.microservice.userssecurity.exceptions.InvalidAddressException;
import com.microservice.userssecurity.model.Address;
import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Optional;


@Component
public class Validations {
    @Autowired
    private IUserRepository userRepo;
    public String validateEmail(String email) {
        // Validar longitud máxima de 30 caracteres
        if (email.length() > 30) {
            return "Email exceeds maximum length of 30 characters.";
        }

        // Validar presencia de "@" y "."
        if (!email.contains("@") || !email.contains(".")) {
            return "Email must contain '@' and '.' symbols.";
        }

        // Dividir el correo electrónico en partes antes y después del "@"
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "Email format is incorrect.";
        }

        String localPart = parts[0]; // Parte local antes del "@"
        String domainPart = parts[1]; // Parte de dominio después del "@"

        // Validar que la parte local tenga al menos 3 caracteres
        if (localPart.length() < 3) {
            return "At least 3 characters are required before the '@'";
        }

        // Validar que entre el "@" y el "." existan al menos 3 letras
        int atIndex = domainPart.indexOf('@');
        int dotIndex = domainPart.indexOf('.');
        if (dotIndex - atIndex < 4) {
            return "There must be at least 3 characters between the '@' and the '.'.";
        }

        // Validar que luego del "." existan al menos 2 caracteres
        if (domainPart.length() - dotIndex - 1 < 2) {
            return "There must be at least 2 characters after the '.'.";
        }

        // Validar que no inicie ni termine con caracteres especiales
        if (!Character.isLetterOrDigit(email.charAt(0)) || !Character.isLetterOrDigit(email.charAt(email.length() - 1))) {
            return "Email cannot start or end with special characters.";
        }

        Optional<UserEntity> existingUser = userRepo.findByEmail(email);
        if (existingUser.isPresent()) {
            return "Email is already in use.";
        }

        return null; // El correo electrónico ha pasado todas las validaciones
    }


    public boolean validatePassword(String password) {
        int minLength = 6;
        int maxLength = 16;

        // Validar longitud de la contraseña
        if (password.length() < minLength || password.length() > maxLength) {
            return false;
        }

        // Validar que la contraseña contenga al menos un número
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // Validar que la contraseña contenga al menos un carácter especial
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return false;
        }

        // Validar que la contraseña contenga al menos una letra mayúscula
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        return true; // La contraseña cumple con todas las validaciones
    }


    public boolean validateName(String name) {
        int minLength = 3;
        int maxLength = 20;

        // Validar longitud del nombre de usuario
        return name.length() >= minLength && name.length() <= maxLength;
    }


}
