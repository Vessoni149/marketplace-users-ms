package com.microservice.userssecurity.service;

import com.microservice.userssecurity.exceptions.AddressNotFoundException;
import com.microservice.userssecurity.exceptions.UserNotFoundException;
import com.microservice.userssecurity.model.Address;
import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.IAddressRepository;
import com.microservice.userssecurity.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AddressService implements IAddressService{
    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IAddressRepository addressRepository;

    public Set<Address> getAddressesFromUser(int userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        Set<Address> addresses = user.getAddresses();
        if (addresses.isEmpty()) {
            throw new AddressNotFoundException("No addresses found for user with ID " + userId);
        }

        return addresses;
    }


    public Address addAddressToUser(int userId, Address address) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address savedAddress = addressRepository.save(address);
        user.getAddresses().add(savedAddress);
        userRepo.save(user);

        return savedAddress;
    }

    public Map<String, Object> removeAddressFromUser(int userId, int addressId) {
        Optional<UserEntity> optionalUser = userRepo.findById(userId);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            Optional<Address> optionalAddress = addressRepository.findById(addressId);
            if (optionalAddress.isPresent()) {
                Address address = optionalAddress.get();
                try {
                    user.getAddresses().remove(address);
                    addressRepository.delete(address);
                    userRepo.save(user);
                    Map<String, Object> response = new HashMap<>();
                    response.put("message", "Address removed successfully");
                    return response;
                } catch (ObjectOptimisticLockingFailureException e) {
                    // Manejar la excepción de conflicto de datos
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", "Error deleting address due to data conflict");
                    return response;
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Address not found");
                return response;
            }
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User not found");
            return response;
        }
    }
}
