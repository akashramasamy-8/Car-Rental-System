package com.ccd.controller;

import java.util.*;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.User;
import com.ccd.model.UserInfo;
import com.ccd.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService userService;

	// Handle role selection (POST)
	@PostMapping("/selectRole")
	public ResponseEntity<?> handleRoleSelection(@RequestParam(value = "role", required = false) String role) {
		// Validate if the role is null or empty
		if (role == null || role.isBlank()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("role", "Please select a role before continuing.");
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		// Check the role value
		if ("admin".equalsIgnoreCase(role)) {
			return new ResponseEntity<>("/auth/adminLogin", HttpStatus.OK);
		} else if ("user".equalsIgnoreCase(role)) {
			return new ResponseEntity<>("/auth/login", HttpStatus.OK);
		}

		// Invalid role value
		Map<String, String> errors = new HashMap<>();
		errors.put("role", "Invalid role selected.");
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	// Admin registration
		@PostMapping("/registerasAdmin")
		public ResponseEntity<?> registerAdmin(@RequestParam("username") String username, 
		                                       @RequestParam("password") String password) {
		    if (userService.registerAdmin(username, password)) {
		        return new ResponseEntity<>("Admin registered successfully.", HttpStatus.CREATED);
		    }
		    return new ResponseEntity<>("Admin registration failed. Username might already exist.", HttpStatus.BAD_REQUEST);
		}


		// Admin login
		@PostMapping("/adminasLogin")
		public ResponseEntity<?> adminLogin(@RequestParam("username") String username,
				@RequestParam("password") String password, HttpSession session) {
			if (username == null || username.isBlank() || password == null || password.isBlank()) {
				Map<String, String> errors = new HashMap<>();
				if (username == null || username.isBlank())
					errors.put("username", "Username is required.");
				if (password == null || password.isBlank())
					errors.put("password", "Password is required.");
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}

			if (userService.loginAsAdmin(username, password)) {
				session.setAttribute("role", "admin");
				return new ResponseEntity<>("Admin logged in successfully.", HttpStatus.OK);
			}
			return new ResponseEntity<>("Invalid admin credentials.", HttpStatus.UNAUTHORIZED);
		}

	// Fetch all users
	@GetMapping("/getAllUsers")
	public ResponseEntity<?> getAllUsers(HttpSession session) {
		if (!"admin".equals(session.getAttribute("role"))) {
			return new ResponseEntity<>("Access denied. Only admins can view user details.", HttpStatus.FORBIDDEN);
		}

		List<User> users = userService.getAllUsers();
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	// User login
	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestParam("username") String username,
			@RequestParam("password") String password, HttpSession session) {
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			Map<String, String> errors = new HashMap<>();
			if (username == null || username.isBlank())
				errors.put("username", "Username is required.");
			if (password == null || password.isBlank())
				errors.put("password", "Password is required.");
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		if (userService.loginAsUser(username, password)) {
			session.setAttribute("role", "user");
			session.setAttribute("username", username);
			return new ResponseEntity<>("Login successful.", HttpStatus.OK);
		}
		return new ResponseEntity<>("Invalid username or password.", HttpStatus.UNAUTHORIZED);
	}

	// User registration
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		try {
			userService.registerUser(user.getUsername(), user.getPassword());
			return new ResponseEntity<>("Registration successful.", HttpStatus.CREATED);
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// Logout endpoint
	@GetMapping("/logout")
	public ResponseEntity<Void> logout(HttpSession session) {
		session.invalidate(); // Invalidate the session

		// Redirect to the frontend's logout loading page
		HttpHeaders headers = new HttpHeaders();
		headers.set("Location", "http://localhost:8080/auth/logoutLoading"); // Frontend URL
		return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 Redirect
	}

	// Logout loading page endpoint
	@GetMapping("/logoutLoading")
	public ResponseEntity<Void> logoutLoadingPage() {
		// Redirect to the frontend role selection page after showing loading page
		HttpHeaders headers = new HttpHeaders();
		headers.set("Location", "http://localhost:8080/admin"); // Frontend URL
		return new ResponseEntity<>(headers, HttpStatus.FOUND); // HTTP 302 Redirect
	}

//    // Login - authenticate and return user info including role
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserInfo user)  {
//		return null;
//       
//    }

}

//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.ccd.exception.InvalidEntityException;
//import com.ccd.model.UserInfo;
//import com.ccd.service.UserService;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:8080")
//public class UserContoller {
//	
//	
//	 	@Autowired
//	    private UserService userService;
//
//	    
//
//	    // Login - authenticate and return user info including role
//	    @PostMapping("/login")
//	    public ResponseEntity<?> login(@RequestBody UserInfo user)  {
//			return null;
//	       
//	    }
//
//	   
//	   
//}
