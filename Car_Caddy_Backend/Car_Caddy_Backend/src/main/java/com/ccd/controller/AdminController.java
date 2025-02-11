package com.ccd.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ccd.exception.AdminAlreadyExistException;
import com.ccd.exception.AdminNotFoundException;
import com.ccd.exception.EmployeeNotFoundException;
import com.ccd.exception.PasswordNotFoundException;
import com.ccd.model.Admin;
import com.ccd.model.Employee;
import com.ccd.repository.AdminRepository;
import com.ccd.service.AdminService;
import com.ccd.service.EmployeeService;

import jakarta.validation.Valid;

//@CrossOrigin(origins = "http://localhost:8081") // Allow requests from frontend
@RestController
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private EmployeeService employeeService;

//    @PostMapping("/login")
//    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> requestBody,BindingResult result) {
//    	
//        String email = requestBody.get("email");
//        String password = requestBody.get("password");
//
//        Map<String, String> response = adminService.login(email, password);
//        
//    }

	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> requestBody, BindingResult result) {
		try {
			// Extracting email and password from the request
			String email = requestBody.get("email");
			String password = requestBody.get("password");

			// Call the service and return the response
			return adminService.login(email, password);

		} catch (AdminNotFoundException | PasswordNotFoundException e) {
			// Return error response if an exception occurs
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> adminRegister(@Valid @RequestBody Admin admin, BindingResult result) {
		if (result.hasErrors()) {
			// Collect validation error messages
			Map<String, String> errors = new HashMap<>();
			result.getFieldErrors().forEach(error -> {
				errors.put(error.getField(), error.getDefaultMessage());
			});

			// Return a bad request with validation error messages
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // This is fine as ResponseEntity<Object>
		}
		Map<String, String> response = adminService.register(admin);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/searchEmployee")
	public ResponseEntity<?> searchEmployee(@RequestBody Map<String, String> requestBody, BindingResult result) {
		String empId = requestBody.get("employeeId");

		try {
			Employee employee = adminService.findEmployeeById(empId);
			return new ResponseEntity<>(employee, HttpStatus.OK);
		} catch (EmployeeNotFoundException ex) {
			// Handle custom exception for employee not found
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", ex.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		} catch (IllegalArgumentException ex) {
			// Handle invalid input
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", ex.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

	}

	@PostMapping("/search")
	public ResponseEntity<?> searchEmployee(@RequestBody Map<String, String> requestBody) {
		String empId = requestBody.get("employeeId");
		String name = requestBody.get("employeeName");

		try {
			// Ensure that at least one of the parameters is provided
			if ((empId == null || empId.isEmpty()) && (name == null || name.isEmpty())) {
				return new ResponseEntity<>("Either employee ID or name must be provided.", HttpStatus.BAD_REQUEST);
			}

			// Call service method to search by ID or Name
			Object result = adminService.searchEmployee(empId, name);

			// Ensure that the result is wrapped in a list if it's a single employee object
			if (result instanceof Employee) {
				result = List.of(result); // Wrap the single employee in a list
			}

			return new ResponseEntity<>(result, HttpStatus.OK);
		} catch (EmployeeNotFoundException ex) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", ex.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
		} catch (IllegalArgumentException ex) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", ex.getMessage());
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}

//    // Global exception handler for specific exceptions
//    @ExceptionHandler(AdminNotFoundException.class)
//    public ResponseEntity<String> handleAdminNotFoundException(AdminNotFoundException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(AdminAlreadyExistException.class)
//    public ResponseEntity<String> handleAdminAlreadyExistsException(AdminAlreadyExistException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//    }
//
//    // Handle other unexpected exceptions
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleException(Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}
