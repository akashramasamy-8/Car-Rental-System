package com.ccd.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ccd.model.Car;

@Controller
public class CarController {

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//
//	}

//	@RequestMapping(value = "/")
	@GetMapping("/home")
	public String home() {
		return "home";
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}

	@GetMapping("/")
	public String registrationPage(Model model) {

		model.addAttribute("car", new Car());// default null. car binding
		return "CarRegistration";// application.properties config

	}

	@Bean // BEAN COMPONENT OBJ AUTOMATICALLY CREATES ( FIN CLIENT OR restTemplate )
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@PostMapping("/carregister")
	public String submitVehicleFormPage(@ModelAttribute("car") Car car, Model model) {
		String url = "http://localhost:9090/car/add";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		HttpEntity<Car> request = new HttpEntity<>(car, headers);

		try {
			// Attempt to send the request to the backend
			restTemplate().exchange(url, HttpMethod.POST, request, Car.class);
			model.addAttribute("successMessage", "Vehicle registered successfully!");
//			return "statuspage";
			return "statuspage2";
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				// Parse validation errors from backend response
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("validationErrors", new HashMap<>()); // Fallback to empty errors map
				model.addAttribute("errorMessage", "An unexpected error occurred.");
			}
			model.addAttribute("car", car); // Preserve the form data
			return "CarRegistration";
		}
	}

//	@RequestMapping(value = "/viewAllVehicles", method = RequestMethod.GET)

	@GetMapping("/viewAllVehicles")
	public String viewAllVehicles(Model model) {
		List<Car> car = new ArrayList<Car>();
		String url = "http://localhost:9090/car/viewAll";

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Car> request = new HttpEntity<>(headers);

//	    try {
		ResponseEntity<List<Car>> response = restTemplate().exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Car>>() {
				});
		car = response.getBody();

//	    } catch (HttpClientErrorException | HttpServerErrorException e) {
//	        model.addAttribute("errorMessage", "Unable to fetch vehicles. Please try again later.");
//	       // return "statuspage";
//		return "statuspage2";
//	    }
		car.forEach(System.out::println);
		model.addAttribute("vehicles", car);
		if (car != null && car.size() != 0) {
			System.out.println("inside if of showall");
			model.addAttribute("vehicles", car);
			return "carlist";
		} else {
			model.addAttribute("errorMessage", "No record found!!!");
//			return "statuspage";
			return "statuspage2";
		}
	}

	@GetMapping("/view")
	public String viewByStatusPage(@ModelAttribute("status") String status, Model model) {

		model.addAttribute("status", new Car());
		return "findbystatus";

	}

//	@RequestMapping(value = "/findByStatus", method = RequestMethod.GET)

	@GetMapping("/viewByStatus")
	public String findVehicleByStatus(@RequestParam(required = false) String status, Model model) {
		if (status == null || status.isBlank()) {
			Map<String, String> validationErrors = new HashMap<>();
			validationErrors.put("status", "Please select a valid status.");
			model.addAttribute("validationErrors", validationErrors); // Pass validation errors to the model
			model.addAttribute("status", ""); // Initialize the status field
			return "findbystatus";
		}

		String url = "http://localhost:9090/car/viewByStatus?status=" + status;
		try {
			ResponseEntity<List<Car>> response = restTemplate().exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Car>>() {
					});
			List<Car> cars = response.getBody();

			model.addAttribute("vehicles", cars);
			if (cars != null && !cars.isEmpty()) {
				return "carlist";
			} else {
				model.addAttribute("errorMessage", "No vehicles found with the given status.");
//				return "statuspage";
				return "statuspage2";
			}
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors); // Pass backend validation errors
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred.");
			}
			model.addAttribute("status", status); // Ensure status field is retained
			return "findbystatus";
		} catch (HttpClientErrorException.NotFound e) {
			model.addAttribute("errorMessage", "No vehicles found with the given status.");
//			return "statuspage";
			return "statuspage2";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
//			return "statuspage";
			return "statuspage2";
		}
	}

	@GetMapping("/viewbytype")
	public String viewByTypePage(@ModelAttribute("type") String type, Model model) {

		model.addAttribute("type", new Car());
		return "findbytype";

	}

//	@RequestMapping(value = "/findCarsByVehicleType", method = RequestMethod.GET)

	@GetMapping("/findCarsByVehicleType")
	public String findCarsByVehicleType(@RequestParam(required = false) String vehicleType, Model model) {
		if (vehicleType == null || vehicleType.isBlank()) {
			Map<String, String> validationErrors = new HashMap<>();
			validationErrors.put("vehicleType", "Please select a valid vehicle type.");
			model.addAttribute("validationErrors", validationErrors);
			model.addAttribute("vehicleType", ""); // Retain the vehicleType field
			return "findbytype";
		}

		String url = "http://localhost:9090/car/viewByVehicleType?vehicleType=" + vehicleType;
		try {
			ResponseEntity<List<Car>> response = restTemplate().exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<Car>>() {
					});
			List<Car> cars = response.getBody();

			model.addAttribute("vehicles", cars);
			if (cars != null && !cars.isEmpty()) {
				return "carlist";
			} else {
				model.addAttribute("errorMessage", "No vehicles found with the given type.");
//				return "statuspage";
				return "statuspage2";
			}
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred while processing validation errors.");
			}
			return "findbytype";
		} catch (HttpClientErrorException.NotFound e) {
			model.addAttribute("errorMessage", "No vehicles found with the given type.");
//			return "statuspage";
			return "statuspage2";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
//			return "statuspage";
			return "statuspage2";
		}
	}

	@GetMapping("/viewbyID")
	public String viewByIDPage(@ModelAttribute("vehicleId") String vehicleId, Model model) {

		model.addAttribute("vehicleId", new Car());
		return "findbyId";

	}

	@GetMapping("/findCarsByVehicleID")
	public String updateCarForm(@RequestParam(required = false) Integer vehicleId, Model model) {
		if (vehicleId == null) {
			Map<String, String> validationErrors = new HashMap<>();
			validationErrors.put("vehicleId", "Vehicle ID is required.");
			model.addAttribute("validationErrors", validationErrors);
			model.addAttribute("vehicleId", ""); // Retain the input field value
			return "findbyid";
		}

		String fetchUrl = "http://localhost:9090/car/viewByVehicleID?vehicleId=" + vehicleId;
		try {
			ResponseEntity<Car> response = restTemplate().exchange(fetchUrl, HttpMethod.GET, null, Car.class);
			Car car = response.getBody();

			model.addAttribute("car", car);
			return "updateCarDetails";
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred while processing validation errors.");
			}
			return "findbyid";
		} catch (HttpClientErrorException.NotFound e) {
			model.addAttribute("errorMessage", "Car with Vehicle ID " + vehicleId + " not found.");
//			return "statuspage";
			return "statuspage2";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
//			return "statuspage";
			return "statuspage2";
		}
	}

	// Handle the update submission
	@PostMapping("/updateCarDetails")
	public String submitUpdatedCarDetails(@ModelAttribute("car") Car car, Model model) {
		String updateUrl = "http://localhost:9090/car/update/" + car.getCarId();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<Car> request = new HttpEntity<>(car, headers);

		try {
			ResponseEntity<Car> response = restTemplate().exchange(updateUrl, HttpMethod.PUT, request, Car.class);
			Car updatedCar = response.getBody();
			model.addAttribute("successMessage", "Car details updated successfully!");
			model.addAttribute("car", updatedCar);
//			return "statuspage";
			return "statuspage2";
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				// Parse validation errors from backend
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred while processing validation errors.");
			}
			model.addAttribute("car", car); // Ensure the car object is retained
			return "updateCarDetails";
		} catch (HttpClientErrorException.NotFound e) {
			model.addAttribute("errorMessage", "Car with ID " + car.getCarId() + " not found.");
//			return "statuspage";
			return "statuspage2";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
//			return "statuspage";
			return "statuspage2";
		}
	}

	@GetMapping("/viewbyVehicleID")
	public String viewByVehicleIDPage(@ModelAttribute("vehicleId") String vehicleId, Model model) {

		model.addAttribute("vehicleId", new Car());
		return "findbyVehicleId";

	}

	@GetMapping("/findCarsByID")
	public String deleteVehicleForm(@RequestParam(required = false) Integer vehicleId, Model model) {
		if (vehicleId == null) {
			Map<String, String> validationErrors = new HashMap<>();
			validationErrors.put("vehicleId", "Vehicle ID is required.");
			model.addAttribute("validationErrors", validationErrors);
			model.addAttribute("vehicleId", ""); // Retain the input field value
			return "findbyid";
		}

		String fetchUrl = "http://localhost:9090/car/viewByVehicleID?vehicleId=" + vehicleId;
		try {
			ResponseEntity<Car> response = restTemplate().exchange(fetchUrl, HttpMethod.GET, null, Car.class);
			Car car = response.getBody();

			model.addAttribute("car", car);
			return "deleteCarDetails";
		} catch (HttpClientErrorException.BadRequest e) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				Map<String, String> errors = objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<>() {
				});
				model.addAttribute("validationErrors", errors);
			} catch (Exception ex) {
				model.addAttribute("errorMessage", "An unexpected error occurred while processing validation errors.");
			}
			return "findbyid";
		} catch (HttpClientErrorException.NotFound e) {
			model.addAttribute("errorMessage", "Car with Vehicle ID " + vehicleId + " not found.");
//			return "statuspage";
			return "statuspage2";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
//			return "statuspage";
			return "statuspage2";
		}
	}

	@PostMapping("/deleteCarsByVehicleID")
	public String deleteCarForm(@RequestParam("vehicleId") int vehicleId, Model model) {
		String deleteUrl = "http://localhost:9090/car/delete/" + vehicleId;

		try {
			// Call delete endpoint
			restTemplate().exchange(deleteUrl, HttpMethod.DELETE, null, String.class);
			model.addAttribute("successMessage", "Car with Vehicle ID " + vehicleId + " deleted successfully.");
		} catch (RestClientException e) {
			model.addAttribute("errorMessage",
					"Failed to delete car with Vehicle ID " + vehicleId + ": " + e.getMessage());
		}

//		return "statuspage";
		return "statuspage2";
	}

}

//import com.carcaddy.model.Car;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.Map;
//import java.util.HashMap;
//@Controller
//@RequestMapping("/car")
//public class CarController {
//	//this is frontend
//  @Value("${backend.base-url}")
//  private String backendBaseUrl;
//
//  private final RestTemplate restTemplate = new RestTemplate();
//
//  // Display the form to add a new car
//  @GetMapping("/addCar")
//  public String showAddCarForm(Model model) {
//      model.addAttribute("car", new Car());
//      return "addCar5";
//  }
//  @GetMapping("/home")
//  public String showHome(Model model) {
//      model.addAttribute("car", new Car());
//      return "home5";
//  }
//
//
//  // Handle the submission of the add car form
//  @PostMapping("/addCarDetails")
//  public String addCarDetails(@ModelAttribute Car car, BindingResult bindingResult, Model model) {
//      try {
//          String url = backendBaseUrl + "/car/addCarDetails";
//          String response = restTemplate.postForObject(url, car, String.class);
//          model.addAttribute("message", response);
//          return "redirect:/car/allCar"; // Redirect to car list after successful submission
//      } catch (HttpClientErrorException e) {
//          // Handle backend validation errors
//          Map<String, String> errors = extractErrorsFromException(e);
//          for (Map.Entry<String, String> entry : errors.entrySet()) {
//              if (entry.getKey().equals("error")) {
//                  model.addAttribute("error", entry.getValue()); // Global error
//              } else {
//                  bindingResult.rejectValue(entry.getKey(), "", entry.getValue()); // Field-specific error
//              }
//          }
//      } catch (Exception e) {
//          model.addAttribute("error", "Error adding car: " + e.getMessage());
//      }
//      return "addCar5"; // Return to the form with errors
//  }
//
//  // Extract errors from the backend exception
//  private Map<String, String> extractErrorsFromException(HttpClientErrorException e) {
//      ObjectMapper objectMapper = new ObjectMapper();
//      try {
//          return objectMapper.readValue(e.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {});
//      } catch (JsonProcessingException ex) {
//          ex.printStackTrace();
//          return Map.of("error", e.getMessage()); // Fallback to global error
//      }
//  }
//
//  // Display all cars
//  @GetMapping("/allCar")
//  public String getAllCars(Model model) {
//      try {
//          // Fetch all cars from the backend
//          String url = backendBaseUrl + "/car/allCar";
//          Car[] cars = restTemplate.getForObject(url, Car[].class);
//
//          // Fetch maintenance count for each car
//          Map<Long, Long> maintenanceCountMap = new HashMap<>();
//          for (Car car : cars) {
//              String countUrl = backendBaseUrl + "/maintenance/maintenanceCountByCar/" + car.getCarId();
//              Long count = restTemplate.getForObject(countUrl, Long.class);
//              maintenanceCountMap.put(car.getCarId(), count);
//          }
//
//          // Add cars and maintenance counts to the model
//          model.addAttribute("cars", cars);
//          model.addAttribute("maintenanceCountMap", maintenanceCountMap);
//      } catch (Exception e) {
//          model.addAttribute("error", "Error fetching cars: " + e.getMessage());
//      }
//      return "carList5";
//  }
//
//  // Display car details by ID
//  @GetMapping("/{carId}")
//  public String getCarDetails(@PathVariable Long carId, Model model) {
//      try {
//          String url = backendBaseUrl + "/car/" + carId;
//          Car car = restTemplate.getForObject(url, Car.class);
//          model.addAttribute("car", car);
//      } catch (Exception e) {
//          model.addAttribute("error", "Error fetching car details: " + e.getMessage());
//      }
//      return "carDetails5"; // You can create a new Thymeleaf template for this
//  }
//}
