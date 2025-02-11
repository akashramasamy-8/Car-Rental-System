package com.ccd.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ccd.exception.AdminAlreadyExistException;
import com.ccd.exception.AdminNotFoundException;
import com.ccd.exception.EmployeeNotFoundException;
import com.ccd.exception.PasswordNotFoundException;
import com.ccd.model.Admin;
import com.ccd.model.Employee;
import com.ccd.repository.AdminRepository;
import com.ccd.repository.EmployeeRepository;

@Service
public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	// Admin login logic
	public ResponseEntity<?> login(String email, String password) {
		// Prepare a response map
		Map<String, String> response = new HashMap<>();

		// Fetch the admin by email
		Optional<Admin> existingAdmin = adminRepository.findByEmail(email);

		// Check if admin exists
		if (existingAdmin.isPresent()) {
			Admin foundAdmin = existingAdmin.get();

			// Validate the password
			if (foundAdmin.getPassword().equals(password)) {
				// Return the admin object and a success status
				return new ResponseEntity<>(foundAdmin, HttpStatus.OK);
			} else {
				// Throw exception for incorrect password
				throw new PasswordNotFoundException("Incorrect password.");
			}
		} else {
			// Throw exception for admin not found
			throw new AdminNotFoundException("Admin not found.");
		}
	}

	// Admin registration logic
	public Map<String, String> register(Admin admin) {
		Map<String, String> response = new HashMap<>();

		// Check if the username already exists
		if (adminRepository.findByUsername(admin.getUsername()).isPresent()) {
			throw new AdminAlreadyExistException("Username already exists.");
		}

		if (adminRepository.findByEmail(admin.getEmail()).isPresent()) {
			throw new AdminAlreadyExistException("Email already exists.");
		}

		adminRepository.save(admin);
		response.put("message", "Admin registered successfully!");

		return response;
	}

	// find employee by id
	public Employee findEmployeeById(String empId) {
		if (empId == null || empId.isEmpty()) {
			throw new IllegalArgumentException("Employee ID must not be empty.");
		}

		try {
//            int id = Integer.parseInt(empId);
			long id = Long.parseLong(empId);
			return employeeRepository.findById(id)
					.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Invalid employee ID format: " + empId);
		}
	}

	// find employee by name or id
	public Object searchEmployee(String empId, String name) {
		if ((empId == null || empId.isEmpty()) && (name == null || name.isEmpty())) {
			throw new IllegalArgumentException("Either employee ID or name must be provided.");
		}

		if (empId != null && !empId.isEmpty()) {
			try {
				long id = Long.parseLong(empId); // Parse the employee ID
				// Search by employee ID
				return employeeRepository.findById(id)
						.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + empId));
			} catch (NumberFormatException ex) {
				throw new IllegalArgumentException("Invalid employee ID format: " + empId);
			}
		} else if (name != null && !name.isEmpty()) {
			// Search by employee name
			List<Employee> employees = employeeRepository.findByEmployeeNameContainingIgnoreCase(name);
			if (employees.isEmpty()) {
				throw new EmployeeNotFoundException("No employees found with name: " + name);
			}
			return employees; // Return the list of employees matching the name
		}

		// Fallback if no valid input was found
		throw new IllegalArgumentException("Invalid search parameters.");
	}

}
