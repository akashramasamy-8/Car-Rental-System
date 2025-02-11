package com.ccd.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Customer;
import com.ccd.model.LoginRequest;

public interface CustomerService {

	public Customer findByCustomerId(long customerId) throws InvalidEntityException;

	public Customer registerCustomer(Customer customer);

	public List<Customer> getAllCustomers() throws InvalidEntityException;

	public Optional<Customer> getCustomerById(long id) throws InvalidEntityException;

	public Customer updateCustomer(long id, Map<String, Object> updatedCustomerData) throws InvalidEntityException;

	public String blockCustomer(long id) throws InvalidEntityException;

	public Customer login(LoginRequest loginRequest) throws InvalidEntityException;

}
