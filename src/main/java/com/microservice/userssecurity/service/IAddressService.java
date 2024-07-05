package com.microservice.userssecurity.service;

import com.microservice.userssecurity.model.Address;

import java.util.Map;
import java.util.Set;

public interface IAddressService {
    public Address addAddressToUser(int userId, Address address);
    public Map<String, Object> removeAddressFromUser(int userId, int addressId);
    public Set<Address> getAddressesFromUser(int userId);
}
