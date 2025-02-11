package com.ccd.controller;

//import  org.springframework.http.HttpStatus;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties.ShowSummary;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.carrentalsystem.Model.Employee;
//import com.project.carrentalsystem.Service.EmployeeService;
//
//@Controller
//public class HomeController {
//
//	
//	@Autowired
//	private EmployeeService employeeService;
//	
//	@GetMapping("/")
//	public String homePage(Model model)
//	{
//		return "index";
//	}
//	
//	
//	
//	@PostMapping("/registerEmployee") 
//  public String addEmployee(@ModelAttribute("employee") Employee employee) {
//      Employee savedEmployee = employeeService.addEmployee(employee);
//      System.out.println("............................."+employee.getAccountType());
//      System.out.println("saved employee ----------------"+savedEmployee);
//      return "registrationSuccess"; 
//  }
//	
//	
//	 	@GetMapping("/registerEmployee")
//	    public String showRegisterEmployeePage(Model model) {
//	        model.addAttribute("employee", new Employee());
//	        return "registerEmployee";
//	    }
//}

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ccd.model.Employee;
import com.ccd.model.Rent_Booking;
import com.ccd.model.UserInfo;

import jakarta.servlet.http.HttpSession;

import org.springframework.core.ParameterizedTypeReference;
import org.eclipse.tags.shaded.org.apache.bcel.generic.NEW;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpHeaders;

import java.util.*;
import java.text.*;
import java.time.temporal.ChronoUnit;

@Controller
public class HomeController {

	private final RestTemplate restTemplate;

	// Injecting backend URL from application.properties

	private String backendUrl = "http://localhost:9090";

	// Constructor injection of RestTemplate
	public HomeController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("///")
	public String homePage(Model model) {
		model.addAttribute("userInfo", new UserInfo());
		// return "index";
		return "main1";
	}

	@GetMapping("/registerEmployee")
	public String registerEmployee(Model model) {
		model.addAttribute("employee", new Employee());
		return "registerEmployee";
	}

	@PostMapping("/registerEmployee")
	public String addEmployee(@ModelAttribute Employee employee, Model model, BindingResult result) {
		System.out.println("INside register" + employee.getEmployeeEmail());
		System.out.println("INside register" + employee.getEmployeeName());
		System.out.println("INside register" + employee.getAccountType());
		System.out.println("INside register" + employee.getDesignation());
		System.out.println("INside register" + employee.getDateOfBirth());
		System.out.println("INside register" + employee);
		String apiEndpoint = backendUrl + "/registerEmployee";

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(apiEndpoint, new HttpEntity<>(employee),
					Map.class);
			System.out.println("response************" + response);

			if (response.getStatusCode().is2xxSuccessful()) {
				Map<String, Object> responseBody = response.getBody();
				// String employeeId = responseBody.get("employeeId").toString();
				String defaultPassword = responseBody.get("defaultPassword").toString();
				String employeeId = responseBody.get("employeeId").toString();

				model.addAttribute("message", "Employee Register successfully!");
				// model.addAttribute("employeeId", employeeId);
				model.addAttribute("defaultPassword", defaultPassword);
				model.addAttribute("employeeId", employeeId);

				// return "redirect:/loginPage?email=" + employeeEmail + "&defaultPassword=" +
				// defaultPassword;
				return "registerEmployee";

			} else {
				model.addAttribute("error", "Failed to add employee. Please try again.");
			}
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
			//

			// Map backend errors to BindingResult
			for (Map.Entry<String, String> entryset : errors.entrySet()) {
				String field = entryset.getKey();
				String errorMsg = entryset.getValue();
				result.rejectValue(field, "", errorMsg);
			}

		}

		return "registerEmployee";
		// return "index";
	}

	@GetMapping("/firstLogin")
	public String firstLoginPage(@RequestParam String empId, @RequestParam String defaultPassword, Model model) {

		return "loginPage";
	}

	@PostMapping("/firstLogin")
	public String handleFirstLogin(@RequestParam Map<String, String> allParams, Model model) {
		String employeeId = allParams.get("employeeId");
		String defaultPassword = allParams.get("defaultPassword");
		String newPassword = allParams.get("newPassword");

		model.addAttribute("defaultPassword", defaultPassword);
		model.addAttribute("employeeId", employeeId);

		String apiEndpoint = backendUrl + "/firstLogin/" + employeeId;

		System.out.println("email: " + employeeId);
		System.out.println("oPass: " + defaultPassword);
		System.out.println("nPass: " + newPassword);

		// Prepare password data to send in the request body
		Map<String, String> passwordData = new HashMap<>();
		passwordData.put("defaultPassword", defaultPassword);
		passwordData.put("newPassword", newPassword);
		System.out.println(passwordData.get("defaultPassword"));
		System.out.println(passwordData.get("newPassword"));

		try {
			HttpEntity<Map<String, String>> request = new HttpEntity<>(passwordData);
			System.out.println("my request: " + request);
			ResponseEntity<String> response = restTemplate.exchange(apiEndpoint, HttpMethod.PUT, request, String.class);
			System.out.println("response is: " + response);

			if (response.getStatusCode().is2xxSuccessful()) {
				// Call the endpoint to fetch employee data
				String employeeApiEndpoint = backendUrl + "/getEmployee/" + employeeId;
				ResponseEntity<Employee> employeeResponse = restTemplate.exchange(employeeApiEndpoint, HttpMethod.GET,
						null, Employee.class);

				// Check if the employee data is present
				if (employeeResponse.getStatusCode().is2xxSuccessful() && employeeResponse.getBody() != null) {
					Employee employee = employeeResponse.getBody();
					model.addAttribute("employee", employee); // Add the employee object to the model
					return "employeeDashboard";
				} else {
					model.addAttribute("errorMessage", "Employee record not found.");
					System.out.println("Emp not found");
					return "statuspage"; // Redirect to the status page if employee not found
				}

			} else {
				model.addAttribute("error",
						"New password must be at least 8 characters long, and include an uppercase letter, a lowercase letter, a number, and a special character.");
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			System.err.println("Error occurred while processing the request: " + e.getResponseBodyAsString());
			model.addAttribute("error",
					"Failed to process the request. Please ensure the password meets the required criteria.");
		}

		return "loginPage";
	}

	@GetMapping("/getAllEmployees")
	public String viewAllEmployees(Model model) {
		System.out.println("INside get employee");
		String url = backendUrl + "/getAllEmployees";

		try {
			// Fetching employee data from backend API
			ResponseEntity<List<Employee>> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Employee>>() {
					});
			System.out.println("response" + response);
			List<Employee> employees = response.getBody();
			if (employees != null && !employees.isEmpty()) {
				model.addAttribute("employees", employees);
				return "dashboard";
			} else {
				model.addAttribute("errorMessage", "No employee records found.");
				return "statuspage";
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Error occurred while fetching employee data. Please try again later.");
			return "statuspage";
		}
	}

	// @GetMapping("/getAllEmployees")
	// public String viewAllEmployees(Model model) {
	// String url = backendUrl + "/getAllEmployees";
	//
	// try {
	// // Fetching employee data from backend API
	// ResponseEntity<List<Employee>> response = restTemplate.exchange(
	// url,
	// HttpMethod.GET,
	// null,
	// new ParameterizedTypeReference<List<Employee>>() {
	// });
	//
	// List<Employee> employees = response.getBody();
	// if (employees != null && !employees.isEmpty()) {
	// model.addAttribute("employees", employees);
	// return "dashboard"; // Returning the "dashboard" view
	// } else {
	// model.addAttribute("errorMessage", "No employee records found.");
	// return "statuspage"; // Returning the error view if no records
	// }
	// } catch (HttpClientErrorException | HttpServerErrorException e) {
	// model.addAttribute("errorMessage", "Error occurred while fetching employee
	// data. Please try again later.");
	// return "statuspage"; // Returning the error view
	// }
	// }

	@GetMapping("/viewProfile")
	public String viewProfile(Model model, HttpSession session) {
		// Retrieve the employee ID from the session
		String empId = (String) session.getAttribute("loggedInEmployeeId");

		if (empId == null) {
			model.addAttribute("errorMessage", "Session expired. Please log in again.");
			return "login1"; // Redirect to login page if session expired
		}

		// Construct backend API endpoint
		String apiEndpoint = backendUrl + "/getEmployeeById/" + empId;

		try {
			// Call backend to fetch employee details
			ResponseEntity<Employee> response = restTemplate.exchange(apiEndpoint, HttpMethod.GET, null,
					Employee.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				Employee employee = response.getBody();
				model.addAttribute("employee", employee); // Add employee details to model
				return "profile"; // Render profile page
			} else {
				model.addAttribute("errorMessage", "Employee not found.");
				return "statuspage"; // Show error if employee not found
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Error occurred while fetching employee profile.");
			return "statuspage"; // Handle error scenarios
		}
	}

	@GetMapping("/displayAllEmployee")
	public String displayAllEmployee(Model model) {
		System.out.println("INside get employee");
		String url = backendUrl + "/getAllEmployees";

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
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("errorMessage", "Error occurred while fetching employee data. Please try again later.");
			return "statuspage";
		}
	}

	@PostMapping("/updateemployee")
	public String updateEmployee(@RequestParam Map<String, String> allParams, Model model) {
		// Retrieve employeeId from URL

		// Retrieve other form data from the request body
		String employeeId = allParams.get("employeeId");
		String name = allParams.get("name");
		String email = allParams.get("email");
		String dob = allParams.get("dob");

		System.out.println("Name: " + name);
		System.out.println("Email: " + email);

		// Prepare the employee data to send to the backend
		Map<String, String> employeeData = new HashMap<>();
		employeeData.put("id", employeeId);
		employeeData.put("name", name);
		employeeData.put("email", email);
		employeeData.put("dob", dob);

		String url = backendUrl + "/updateEmployee"; // Assuming this URL updates the employee on backend

		try {
			HttpEntity<Map<String, String>> request = new HttpEntity<>(employeeData);
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				model.addAttribute("message", "Employee updated successfully!"); // Success message
			} else {
				model.addAttribute("error", "Failed to update employee. Please try again.");
			}

			// Fetch the updated employee details (assuming you want to show them again)
			ResponseEntity<Employee> employeeResponse = restTemplate
					.getForEntity(backendUrl + "/getEmployee/" + employeeId, Employee.class);
			if (employeeResponse.getStatusCode().is2xxSuccessful() && employeeResponse.getBody() != null) {
				model.addAttribute("employee", employeeResponse.getBody());
			} else {
				model.addAttribute("error", "Employee not found.");
				return "errorPage"; // Handle errors if needed
			}

			return "updateEmployee"; // Return the update page with the updated data
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("error", "An error occurred while updating the employee: " + e.getMessage());
			return "updateEmployee"; // Return to the update form with error message
		}
	}

	@GetMapping("/updateemployee/{id}")
	public String showUpdateForm(@PathVariable int id, Model model) {

		String url = backendUrl + "/getEmployee/" + id;

		try {
			// Fetch employee details from the backend
			ResponseEntity<Employee> response = restTemplate.getForEntity(url, Employee.class);
			if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
				Employee employee = response.getBody(); // This gets the employee object from the response

				// Print the employee details to the console
				System.out.println("Employee Response: " + employee);

				// Optionally, print individual fields if the employee object is complex
				System.out.println("Employee Name: " + employee.getEmployeeName());
				System.out.println("Employee ID: " + employee.getEmployeeId());

				model.addAttribute("employee", employee); // Add employee object to the model for the view
				return "updateEmployee"; // Thymeleaf template name
			} else {
				model.addAttribute("errorMessage", "Employee not found.");
				return "errorPage"; // Error page if employee not found
			}
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An error occurred while fetching employee details.");
			return "errorPage";
		}
	}

	@GetMapping("/employeeDashboard")
	public String showEmployeeDashboard(@RequestParam(required = false) String employeeId, Model model) {
		if (employeeId != null) {
			// Fetch employee details using the employeeId
			ResponseEntity<Employee> employeeResponse = restTemplate
					.getForEntity(backendUrl + "/getEmployee/" + employeeId, Employee.class);

			if (employeeResponse.getStatusCode().is2xxSuccessful() && employeeResponse.getBody() != null) {
				model.addAttribute("employee", employeeResponse.getBody());
			} else {
				model.addAttribute("error", "Employee not found.");
			}
		} else {
			model.addAttribute("error", "Employee ID is missing.");
		}

		return "employeeDashboard"; // Render the Employee Dashboard page
	}

}