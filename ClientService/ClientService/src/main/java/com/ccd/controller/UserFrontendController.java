package com.ccd.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ccd.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class UserFrontendController {

	private final RestTemplate restTemplate = new RestTemplate();
	@GetMapping("/notverified")
	public String notverified() {
		return "notverified";
	}
	@GetMapping("/home")
	public String home() {
		return "home";
	}
	
	
	

	// Role selection page (GET)
	@GetMapping("/selectRole")
	public String selectRolePage() {
		return "selectRole"; // Render the role selection page
	}

	// Handle role selection (POST)
	@PostMapping("/selectRole")
	public String handleRoleSelection(@RequestParam(value = "role", required = false) String role, Model model) {
		String url = "http://localhost:9090/auth/selectRole";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		HttpEntity<String> request = new HttpEntity<>("role=" + (role == null ? "" : role), headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				return "redirect:" + response.getBody(); // Redirect to respective login page
			}
		} catch (HttpClientErrorException.BadRequest e) {
			try {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors); // Add validation errors to the model
			} catch (JsonProcessingException ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred while processing validation errors.");
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
		}

		return "selectRole"; // Reload the role selection page with errors
	}

	// Admin login page
			@GetMapping("/adminLogin1")
			public String adminLoginPage() {
				return "adminLogin";
			}

		@PostMapping("/adminLogin1")
		public String adminLogin(@RequestParam("username") String username, @RequestParam("password") String password,
				HttpSession session, Model model) {
			String url = "http://localhost:9090/auth/adminasLogin";

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/x-www-form-urlencoded");

			String body = "username=" + username + "&password=" + password;
			HttpEntity<String> request = new HttpEntity<>(body, headers);

			try {
				ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

				if (response.getStatusCode().is2xxSuccessful()) {
					session.setAttribute("role", "admin"); // Store role in session
					session.setAttribute("username", username); // Store username in session for display
					return "redirect:/auth/adminDashboard"; // Redirect to adminDashboard
				}
			} catch (HttpClientErrorException.BadRequest e) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
					});
					model.addAttribute("validationErrors", errors);
				} catch (Exception ex) {
					model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
				}
			} catch (Exception e) {
				model.addAttribute("errorMessage", "Admin login failed: " + e.getMessage());
			}

			return "adminLogin"; // Reload login page with error message
		}

		@GetMapping("/adminDashboard")
		public String adminDashboard(HttpSession session, Model model) {
			if (!"admin".equals(session.getAttribute("role"))) {
				return "error403"; // Redirect to error page if not admin
			}
			model.addAttribute("username", session.getAttribute("username")); // Pass admin username to the dashboard
			return "adminDashboard";
		}

		// Admin registration page
		@GetMapping("/registerAdmin1")
		public String registerAdminPage() {
			return "registerAdmin";
		}

		// Register admin
		@PostMapping("/registerAdmin1")
		public String registerAdmin(@RequestParam("username") String username, @RequestParam("password") String password,
				Model model) {
			String url = "http://localhost:9090/auth/registerasAdmin";

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/x-www-form-urlencoded");

			HttpEntity<String> request = new HttpEntity<>("username=" + username + "&password=" + password, headers);

			try {
				ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

				if (response.getStatusCode().is2xxSuccessful()) {
					model.addAttribute("successMessage", "Admin registered successfully. Please log in.");
					return "successpageadmin";
				}
			} catch (HttpClientErrorException.BadRequest e) {
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
					});
					model.addAttribute("validationErrors", errors);
				} catch (Exception ex) {
					model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
				}
			} catch (Exception e) {
				model.addAttribute("errorMessage", "Admin registration failed: " + e.getMessage());
			}

			return "registerAdmin";
		}

	// Fetch users
	@GetMapping("/getAllUsers")
	public String getAllUsers(HttpSession session, Model model) {
		// Debug: Log session attributes
		System.out.println("Session Role: " + session.getAttribute("role"));

		// Check if the user is an admin
		if (!"admin".equals(session.getAttribute("role"))) {
			return "error403"; // Forbidden page if not admin
		}

		String url = "http://localhost:9090/auth/getAllUsers"; // Backend endpoint
		try {
			ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<>() {
					});

			List<User> users = response.getBody();
			System.out.println("Users received in frontend: " + users); // Debug log

			if (users != null && !users.isEmpty()) {
				model.addAttribute("users", users);
			} else {
				model.addAttribute("errorMessage", "No users found.");
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Failed to fetch user details: " + e.getMessage());
		}

		return "alldetails"; // Render user details page
	}

	// Show login page
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	// User login
	@PostMapping("/login")
	public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password,
			HttpSession session, Model model) {
		String url = "http://localhost:9090/auth/login";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		HttpEntity<String> request = new HttpEntity<>("username=" + username + "&password=" + password, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				session.setAttribute("role", "user");
				session.setAttribute("username", username);
				return "redirect:/auth/userDashboard";
			}
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Login failed: " + e.getMessage());
		}

		return "login";
	}

	// Show registration page
	@GetMapping("/register")
	public String showRegistrationPage() {
		return "register";
	}

	// Handle registration
	@PostMapping("/register")
	public String registerUser(@RequestParam("username") String username, @RequestParam("password") String password,
			Model model) {
		String url = "http://localhost:9090/auth/register";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		HttpEntity<String> request = new HttpEntity<>("username=" + username + "&password=" + password, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				model.addAttribute("successMessage", "Registration successful. Click below to login.");
				return "successpage";
			}
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
		}

		return "login";
	}

	@GetMapping("/userDashboard")
	public String userDashboard(HttpSession session, Model model) {
		if (!"user".equals(session.getAttribute("role"))) {
			return "error403"; // Redirect to error page if not admin
		}
		model.addAttribute("username", session.getAttribute("username")); // Pass admin username to the dashboard
		return "userDashboard";
	}

	// Logout (Frontend)
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate(); // Invalidate frontend session

		// Redirect to backend logout
		return "redirect:http://localhost:9090/auth/logout";
	}

	// Render the logout loading page
	@GetMapping("/logoutLoading")
	public String showLogoutLoadingPage() {
		return "logoutLoading"; // Render the HTML page for logoutLoading
	}

}
