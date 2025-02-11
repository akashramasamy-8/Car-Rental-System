package com.ccd.controller;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Customer;
import com.ccd.model.LoginRequest;
import com.ccd.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
//@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public ResponseEntity<Customer> registerCustomer(@RequestBody @Validated Customer customer)
			throws InvalidEntityException {
		return new ResponseEntity<>(customerService.registerCustomer(customer), HttpStatus.OK);
	}

	@GetMapping("/get-all-customers")
	public ResponseEntity<List<Customer>> getAllCustomers() throws InvalidEntityException {
		return new ResponseEntity<>(customerService.getAllCustomers(), HttpStatus.OK);
	}

	@GetMapping("/get-customer-by-id/{id}")
	public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable int id) throws InvalidEntityException {
		return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
	}

	@PutMapping("/update-customer/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable int id,
			@RequestBody Map<String, Object> updatedCustomerData) throws InvalidEntityException {
		return new ResponseEntity<>(customerService.updateCustomer(id, updatedCustomerData), HttpStatus.OK);
	}

	@PostMapping("/block-customer/{id}")
	public ResponseEntity<String> blockCustomer(@PathVariable int id) throws InvalidEntityException {
		String response = customerService.blockCustomer(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<Customer> loginCustomer(@RequestBody LoginRequest loginRequest)
			throws InvalidEntityException {
		// Perform your login logic here
		return new ResponseEntity<>(customerService.login(loginRequest), HttpStatus.OK);
	}
}