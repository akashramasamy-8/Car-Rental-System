package com.ccd.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ccd.model.Admin;
import com.ccd.model.AdminLogin;
import com.ccd.model.Employee;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	private String backendUrl = "http://localhost:9090";

	private final RestTemplate restTemplate;

	public AdminController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	// @GetMapping("/login")
	// public String showLoginPage() {
	// return "adminLogin"; // Returns adminLogin.html page
	// }

	@GetMapping("/admin")
	public String admin() {
		return "admin";
	}

	@PostMapping("/login")
	public String loginAdmin(@ModelAttribute AdminLogin adminLogin, Model model, BindingResult result,
			HttpSession session) {
		
//		System.out.println("Admin Login Attempt");
		String apiEndpoint = backendUrl + "/login";

		try {
			// Sending the admin data to the backend
			ResponseEntity<Map> response = restTemplate.postForEntity(apiEndpoint, new HttpEntity<>(adminLogin),
					Map.class);
//			System.out.println("llllllll" + response);
			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> responseBody = response.getBody();

				session.setAttribute("admin", responseBody);

				Object obejct = session.getAttribute("admin");
//				System.out.println(obejct);

				String objectString = obejct.toString();
				String username = objectString.replaceAll(".*username=([^,}]+).*", "$1");
				System.out.println("Admin Controller ----- User Name ----- " + username);
				model.addAttribute("uname", username);

				model.addAttribute("message", "Login successful!");
				return "adminDashboard"; // Redirect to the admin dashboard
			} else {
				model.addAttribute("error", "Invalid credentials.");
			}
		} catch (HttpClientErrorException e) {
			// Parse and display validation errors from backend
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});

				// If error contains a specific message, map it to BindingResult or display it
				// as a general error
				if (errors.containsKey("error")) {
					model.addAttribute("loginError", errors.get("error"));
				}

			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}

			// Map backend errors to BindingResult
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				String field = entry.getKey();
				String errorMsg = entry.getValue();
				result.rejectValue(field, "", errorMsg);
			}
		}
		model.addAttribute("myadminlogin", adminLogin);

		return "adminLogin1"; // Stay on the login page in case of failure
	}

	@GetMapping("/admin/getAllEmployees")
	public String showDashboardPage(HttpSession session, Model model) {
		// Check if the session contains admin details
		if (session.getAttribute("admin") == null) {
			model.addAttribute("error", "Please log in to access the dashboard.");
			return "redirect:/admin/login"; // Redirect to login if not logged in
		}
		return "adminDashboard1"; // Show admin dashboard if logged in
	}

	@GetMapping("/register")
	public String registerAdmin(Model model) {
		model.addAttribute("admin", new Admin());
		return "adminRegister";
	}

	@GetMapping("/login")
	public String loginAdmin(Model model) {
		if (!model.containsAttribute("myadminlogin")) {
			model.addAttribute("myadminlogin", new AdminLogin());
		}
		return "adminLogin1";
	}

	@PostMapping("/register")
	public String registerAdmin(@ModelAttribute Admin admin, Model model, BindingResult result) {
		String apiEndpoint = backendUrl + "/register";

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(apiEndpoint, new HttpEntity<>(admin), Map.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				model.addAttribute("message", "Admin registered successfully!");
				return "redirect:/login"; // Redirect to the login page after registration
			} else {
				model.addAttribute("error", "Registration failed. Please try again.");
			}
		} catch (HttpClientErrorException e) {
			// Parse and display validation errors from backend
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});

				// If error contains a specific message, map it to BindingResult or display it
				// as a general error
				if (errors.containsKey("error")) {
					model.addAttribute("registrationError", errors.get("error"));
				}

			} catch (JsonProcessingException e1) {
				e1.printStackTrace();
			}

			// Map backend errors to BindingResult
			for (Map.Entry<String, String> entry : errors.entrySet()) {
				String field = entry.getKey();
				String errorMsg = entry.getValue();
				result.rejectValue(field, "", errorMsg);
			}
		}

		model.addAttribute("admin", admin);
		return "adminRegister"; // Stay on the registration page in case of failure
	}

	@PostMapping("/searchEmployee")
	public String searchEmployee(@RequestParam("employeeId") String employeeId, Model model) {
		System.out.println("Searching for Employee ID: " + employeeId);
		String apiEndpoint = backendUrl + "/searchEmployee";

		try {
			// Sending the Employee ID to the backend
			ResponseEntity<Employee> response = restTemplate.postForEntity(apiEndpoint,
					new HttpEntity<>(Map.of("employeeId", employeeId)), Employee.class);

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				model.addAttribute("employee", response.getBody()); // Pass the employee details to the view

			} else {
				model.addAttribute("error", "Employee not found.");
			}
		} catch (HttpClientErrorException ex) {
			try {
				// Extract the error message directly from the backend's response
				String errorMessage = new ObjectMapper()
						.readValue(ex.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {
						}).get("error");
				model.addAttribute("error", errorMessage); // Show backend error
			} catch (Exception e) {
				model.addAttribute("error", "Either employee ID or name must be provided.");
			}
		}

		return "adminDashboard1"; // Return to the same dashboard page
	}

	@PostMapping("/removeEmployee")
	public String removeEmployee(@RequestParam("employeeId") String employeeId, Model model, HttpSession session) {

		if (session.getAttribute("admin") == null) {
			return "adminLogin1";
		}
		System.out.println("Removing Employee ID: " + employeeId);
		String apiEndpoint = backendUrl + "/deactivateEmployee/" + employeeId;

		try {
			// Sending the Employee ID to the backend
			ResponseEntity<String> response = restTemplate.exchange(apiEndpoint, HttpMethod.PUT, null, String.class);
			// No body required for this PUT request

			if (response.getStatusCode().is2xxSuccessful()) {
				model.addAttribute("message", response.getBody());
			} else {
				model.addAttribute("error", "Failed to remove the employee.");
			}
		} catch (HttpClientErrorException ex) {
			try {
				// Extract the error message directly from the backend's response
				String errorMessage = new ObjectMapper()
						.readValue(ex.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {
						}).get("error");
				model.addAttribute("error", errorMessage); // Show backend error
			} catch (Exception e) {
				model.addAttribute("error", e.getMessage());
			}
		}

		return "adminDashboard1";
	}

	@GetMapping("/adminDashboard")
	public String showDashboardPage() {
		return "adminDashboard1";
	}

	@PostMapping("/searchEmployeeByIdOrName")
	public String searchEmployeeByIdOrName(@RequestParam String searchQuery, Model model, HttpSession session) {

		System.out.println("Searching for Employee with query: " + searchQuery);
		if (session.getAttribute("admin") == null) {
			return "adminLogin1"; // Redirect if admin is not logged in
		}
		// Assume employeeId is numeric, and employeeName is alphabetic
		boolean isId = searchQuery.matches("\\d+");

		String apiEndpoint = backendUrl + "/search"; // Backend endpoint path

		try {
			// Create request body with Employee ID or Name
			Map<String, String> requestBody = new HashMap<>();
			if (isId) {
				requestBody.put("employeeId", searchQuery);
			} else {
				requestBody.put("employeeName", searchQuery);
			}

			// Make the POST request to the backend API
			ResponseEntity<List<Employee>> response = restTemplate.exchange(apiEndpoint, HttpMethod.POST,
					new HttpEntity<>(requestBody), new ParameterizedTypeReference<List<Employee>>() {
					});

			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				List<Employee> employees = response.getBody();
				model.addAttribute("employees", employees);
				return "getEmployee"; // Render the view
			} else {
				model.addAttribute("error", "Employee not found.");
			}
		} catch (HttpClientErrorException ex) {
			try {
				// Extract the error message directly from the backend's response
				String errorMessage = new ObjectMapper()
						.readValue(ex.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {
						}).get("error");
				model.addAttribute("error", errorMessage); // Show backend error
			} catch (Exception e) {
				model.addAttribute("error", e.getMessage());
			}
		}

		return "getEmployee"; // Render the view
	}

	@GetMapping("/displayDeactivatedEmployees")
	public String displayAllDeactivatedEmployee(Model model, HttpSession session) {

		if (session.getAttribute("admin") == null) {
			return "adminLogin1"; // Redirect if admin is not logged in
		}
		System.out.println("INside get employee");
		String url = backendUrl + "/fetchDeactivatedAccounts";

		try {
			// Fetching employee data from backend API
			ResponseEntity<List<Employee>> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Employee>>() {
					});
			System.out.println("response" + response);
			List<Employee> employees = response.getBody();
			if (employees != null && !employees.isEmpty()) {
				model.addAttribute("employees", employees);
				return "getEmployee";
			} else {
				model.addAttribute("errorMessage", "No employee records found.");
				return "statuspage";
			}
		} catch (HttpClientErrorException ex) {
			try {
				// Extract the error message directly from the backend's response
				String errorMessage = new ObjectMapper()
						.readValue(ex.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {
						}).get("error");
				model.addAttribute("error", errorMessage); // Show backend error
			} catch (Exception e) {
				model.addAttribute("error", e.getMessage());
			}
		}
		return "adminDashboard1";
	}

}
