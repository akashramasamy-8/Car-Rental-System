package com.ccd.controller;

import com.ccd.model.Customer;
import com.ccd.model.Employee;
import com.ccd.model.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CustomerController {

	private String Backend_url = "http://localhost:9090";

	Employee empSession = null;
	Customer custSession = null;
	String role = "admin";

	@ModelAttribute
	public void getEmployee(@SessionAttribute(name = "empObj", required = false) Employee empObj) {
		if (empObj != null) {
			empSession = empObj;
		}
	}

	@ModelAttribute
	public void getCustomer(@SessionAttribute(name = "custObj", required = false) Customer custObj) {
		if (custObj != null) {
			custSession = custObj;
		}
	}

	@ModelAttribute
	public void getRole(@SessionAttribute(name = "role", required = false) String userRole) {
		if (userRole != null) {
			role = userRole;
		}
	}

	@GetMapping("//")
	public String home() {
		return "main";
	}

	@GetMapping("//home")
	public String customerRegister(Model model) {
		model.addAttribute("customer", new Customer());
		return "home3";
	}

	@GetMapping("//login")
	public String customerLogin(Model model) {
		model.addAttribute("loginRequest", new LoginRequest());
		return "login3";
	}

	@GetMapping("/customer/register")
	public String register(Model model) {
		model.addAttribute("customer", new Customer());
		return "register3";
	}

	@PostMapping("/customer/registerCustomer")
	public String registerCustomer(@ModelAttribute("customer") Customer customer, BindingResult result, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(Backend_url + "/customer/register", customer,
					String.class);
			// model.addAttribute("message", response.getBody());
			model.addAttribute("message", "customer registered successfully");
			model.addAttribute("customer", new Customer());
		} catch (HttpClientErrorException e) {
			// Parse and display validation errors from backend
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Map backend errors to BindingResult
			for (Map.Entry<String, String> entryset : errors.entrySet()) {
				String field = entryset.getKey();
				String errorMsg = entryset.getValue();
				result.rejectValue(field, "", errorMsg);
			}

		}
		return "register3";
	}

	@PostMapping("/customer/registeringCustomer")
	public String registeringCustomer(@ModelAttribute("customer") Customer customer, BindingResult result,
			Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(Backend_url + "/customer/register", customer,
					String.class);
			// model.addAttribute("message", response.getBody());
			// model.addAttribute("message", "customer registered successfully");
			// model.addAttribute("customer", new Customer());
		} catch (HttpClientErrorException e) {
			// Parse and display validation errors from backend
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});
			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Map backend errors to BindingResult
			for (Map.Entry<String, String> entryset : errors.entrySet()) {
				String field = entryset.getKey();
				String errorMsg = entryset.getValue();
				result.rejectValue(field, "", errorMsg);
			}
			return "home3";

		}
		model.addAttribute("loginRequest", new LoginRequest());
		return "login3";
	}

	@PostMapping("/customer/login")
	public String loginCustomer(@ModelAttribute("loginRequest") LoginRequest loginRequest, Model model,
			HttpSession session) {

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Customer> response = restTemplate.postForEntity(Backend_url + "/customer/login", // Correct
																											// endpoint
					loginRequest, Customer.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				Customer customer = response.getBody(); // Deserialize directly
				if (customer != null) {
					// Store the customer in the session
					session.setAttribute("customer", customer);
					model.addAttribute("customer", session.getAttribute("customer"));
					return "dashboard"; // Redirect to the dashboard page
				}
			}
		} catch (HttpClientErrorException e) {
			// Handle 4xx errors (e.g., unauthorized login)
			model.addAttribute("errorMessage", "Invalid email or password");
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Invalid email or password");
		}
		return "login3";
	}

	@GetMapping("/dashboard")
	public String showDashboard(HttpSession session, Model model) {
		model.addAttribute("customer", session.getAttribute("customer"));
		return "dashboard";
	}

	@GetMapping("/logout")
	public String logoutCustomer(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("loginRequest", new LoginRequest());
		return "login3";
	}

	@GetMapping("/update")
	public String update(Model model, HttpSession session) {
		model.addAttribute("customer", session.getAttribute("customer"));
		return "update_customer";
	}

	@GetMapping("/customer/get-customer-by-id")
	public String getCustomerById(@RequestParam(value = "id", required = false) Long id, Model model) {
		// Call the backend API
		RestTemplate restTemplate = new RestTemplate();
		if (role != null && role.equals("customer") && id == null && custSession != null) {
			id = custSession.getId();
		}
		if (id != null) {
			try {
				String apiUrl = Backend_url + "/customer/get-customer-by-id/" + id;
				ResponseEntity<Optional<Customer>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
						new ParameterizedTypeReference<Optional<Customer>>() {
						});
				model.addAttribute("role", role);
				if (response.getStatusCode().is2xxSuccessful() && response.getBody().isPresent()) {
					model.addAttribute("customer", response.getBody().get());
				}
			} catch (HttpClientErrorException e) {
				// Handle cases where the backend returns an error or no data
				Map<String, String> errors = null;
				try {
					errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
							new TypeReference<Map<String, String>>() {
							});

					// Map backend error message to Model
					model.addAttribute("errorMessage", errors.get("message"));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return "get-customer-by-id"; // Returns the Thymeleaf template
	}

	@GetMapping("/customer/get-all-customers")
	public String getAllCustomers(Model model) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			String apiUrl = Backend_url + "/customer/get-all-customers";
			ResponseEntity<List<Customer>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Customer>>() {
					});

			List<Customer> customers = response.getBody();
			model.addAttribute("role", role);
			if (customers != null && !customers.isEmpty()) {
				model.addAttribute("customers", customers);
			}

		} catch (HttpClientErrorException e) {
			// Handle cases where the backend returns an error or no data
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});

				// Map backend error message to Model
				model.addAttribute("errorMessage", errors.get("message"));
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return "get-all-customers"; // Returns the Thymeleaf template
	}

	@GetMapping("/customer/block-customer")
	public String blockCustomer(@RequestParam(value = "id", required = false) Integer id, Model model) {
		RestTemplate restTemplate = new RestTemplate();
		// Call the backend API to block the customer
		String apiUrl = Backend_url + "/customer/block-customer/" + id;
		if (id != null) {
			try {
				// Send POST request to the backend
				ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, null, String.class);

				// Check if the response is successful
				if (response.getStatusCode().is2xxSuccessful()) {
					model.addAttribute("message", response.getBody());
				}
			} catch (HttpClientErrorException e) {
				// Handle cases where the backend returns an error or no data
				Map<String, String> errors = null;
				try {
					errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
							new TypeReference<Map<String, String>>() {
							});

					// Map backend error message to Model
					model.addAttribute("message", errors.get("message"));
				} catch (JsonProcessingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return "block-customer"; // Return to the same page or another page with the feedback message
	}

	@GetMapping("/customer/update-customer")
	public String updateCustomer(Model model) {
		model.addAttribute("role", role);
		model.addAttribute("customer", new Customer());
		return "update-customer";
	}

	@PostMapping("/customer/updateCustomer")
	public String updateCustomerDetails(@RequestParam(value = "id") Long id,
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "loyaltyPoints", required = false) Integer loyaltyPoints,
			@RequestParam(value = "blocklistStatus", required = false) Boolean blocklistStatus,
			@RequestParam(value = "password", required = false) String password,
			@ModelAttribute("customer") Customer customer, BindingResult result, Model model,
			@SessionAttribute(name = "role", required = false) String role) {

		if (role != null && role.equals("customer") && id == null && custSession != null) {
			id = custSession.getId();
		}

		Map<String, Object> updatedCustomer = new HashMap<>();
		updatedCustomer.put("id", id);
		if (firstName.length() > 0)
			updatedCustomer.put("firstName", firstName);
		if (lastName.length() > 0)
			updatedCustomer.put("lastName", lastName);
		if (email.length() > 0)
			updatedCustomer.put("email", email);
		if (mobile.length() > 0)
			updatedCustomer.put("mobile", mobile);
		if (gender.length() > 0)
			updatedCustomer.put("gender", gender);
		if (loyaltyPoints != null)
			updatedCustomer.put("loyaltyPoints", loyaltyPoints);
		if (blocklistStatus.booleanValue())
			updatedCustomer.put("blocklistStatus", blocklistStatus);
		if (password.length() > 0)
			updatedCustomer.put("password", password);

		String apiUrl = Backend_url + "/customer/update-customer/" + id;
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT,
					new HttpEntity<>(updatedCustomer), String.class);
			model.addAttribute("role", role);
			model.addAttribute("message", "Customer updated successfully.");
			model.addAttribute("customer", new Customer());
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});

			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (errors.containsKey("message")) {
				model.addAttribute("message", errors.get("message"));
			} else if (errors != null) {
				for (Map.Entry<String, String> entry : errors.entrySet()) {
					String field = entry.getKey();
					String errorMsg = entry.getValue();
					result.rejectValue(field, "", errorMsg);
				}
			}

		}

//        model.addAttribute("customer", new Customer()); // Reset the form
		return "update-customer";
	}

	@PostMapping("/updateCustomer")
	public String updateCustomer(@RequestParam(value = "id") Long id,
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "mobile", required = false) String mobile,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "loyaltyPoints", required = false) Integer loyaltyPoints,
			@RequestParam(value = "blocklistStatus", required = false) Boolean blocklistStatus,
			@RequestParam(value = "password", required = false) String password,
			@ModelAttribute("customer") Customer customer, BindingResult result, Model model, HttpSession session,
			@SessionAttribute(name = "role", required = false) String role) {

		if (role != null && role.equals("customer") && id == null && custSession != null) {
			id = custSession.getId();
		}

		Map<String, Object> updatedCustomer = new HashMap<>();
		updatedCustomer.put("id", id);
		if (firstName.length() > 0)
			updatedCustomer.put("firstName", firstName);
		if (lastName.length() > 0)
			updatedCustomer.put("lastName", lastName);
		if (email.length() > 0)
			updatedCustomer.put("email", email);
		if (mobile.length() > 0)
			updatedCustomer.put("mobile", mobile);
		if (gender.length() > 0)
			updatedCustomer.put("gender", gender);
		if (loyaltyPoints != null)
			updatedCustomer.put("loyaltyPoints", loyaltyPoints);
		if (blocklistStatus.booleanValue())
			updatedCustomer.put("blocklistStatus", blocklistStatus);
		if (password.length() > 0)
			updatedCustomer.put("password", password);

		String apiUrl = Backend_url + "/customer/update-customer/" + id;
		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.PUT,
					new HttpEntity<>(updatedCustomer), String.class);
			// model.addAttribute("role",role);
			// model.addAttribute("message", "Customer updated successfully.");
			// Customer customer1=response.getBody();
			// session.setAttribute("customer",customer1);

			ObjectMapper objectMapper = new ObjectMapper();
			Customer updatedCustomerObj = objectMapper.readValue(response.getBody(), Customer.class);
			session.setAttribute("customer", updatedCustomerObj);
			model.addAttribute("customer", session.getAttribute("customer"));
		} catch (HttpClientErrorException e) {
			Map<String, String> errors = null;
			try {
				errors = new ObjectMapper().readValue(e.getResponseBodyAsString(),
						new TypeReference<Map<String, String>>() {
						});

			} catch (JsonMappingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (errors.containsKey("message")) {
				model.addAttribute("message", errors.get("message"));
			} else if (errors != null) {
				for (Map.Entry<String, String> entry : errors.entrySet()) {
					String field = entry.getKey();
					String errorMsg = entry.getValue();
					result.rejectValue(field, "", errorMsg);
				}
			}

		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

//        model.addAttribute("customer", new Customer()); // Reset the form
		return "dashboard";
	}

}
