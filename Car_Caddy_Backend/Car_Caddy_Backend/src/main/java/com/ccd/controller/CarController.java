package com.ccd.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.InvalidFieldException;
import com.ccd.model.Car;
import com.ccd.repository.CarRepository;
import com.ccd.service.CarService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/car")
public class CarController {

	@Autowired
	private CarService carService;

	@Autowired
	private CarRepository carRepository;

	@PostMapping("/add")
	public ResponseEntity<?> addCar(@Valid @RequestBody Car car, BindingResult result) {
	    if (result.hasErrors()) {
	        Map<String, String> errors = new HashMap<>();
	        for (FieldError error : result.getFieldErrors()) {
	            errors.put(error.getField(), error.getDefaultMessage());
	        }
	        return ResponseEntity.badRequest().body(errors);
	    }
	    return ResponseEntity.ok(carService.addCar(car));
	}


	@PostMapping("/selectRole")
	public ResponseEntity<?> handleRoleSelection(@RequestParam("role") String role) {
		// Validate role input
		if (role == null || role.isBlank()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("role", "Role selection is required.");
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		if ("admin".equalsIgnoreCase(role)) {
			return new ResponseEntity<>("/auth/adminLogin", HttpStatus.OK);
		} else if ("user".equalsIgnoreCase(role)) {
			return new ResponseEntity<>("/auth/login", HttpStatus.OK);
		}

		// If the role is invalid
		Map<String, String> errors = new HashMap<>();
		errors.put("role", "Invalid role selected.");
		return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/viewAll")
	public ResponseEntity<List<Car>> getAllCars() {
		return new ResponseEntity<>(carService.getAllCars(), HttpStatus.OK);
	}

	@GetMapping("/viewByStatus")
	public ResponseEntity<?> findCarsByStatus(@RequestParam(required = false) String status) {
		if (status == null || status.isBlank()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("status", "Status cannot be null or blank.");
			return ResponseEntity.badRequest().body(errors); // Return validation errors
		}

		List<Car> cars = carService.findCarsByStatus(status);
		if (cars.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vehicles found with the given status.");
		}

		return ResponseEntity.ok(cars);
	}

	@GetMapping("/viewByVehicleType")
	public ResponseEntity<?> findCarsByVehicleType(@RequestParam(required = false) String vehicleType) {
		if (vehicleType == null || vehicleType.isBlank()) {
			Map<String, String> errors = new HashMap<>();
			errors.put("vehicleType", "Vehicle type cannot be null or blank.");
			return ResponseEntity.badRequest().body(errors);
		}

		List<Car> cars = carService.findCarsByVehicleType(vehicleType);
		if (cars.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vehicles found with the given type.");
		}

		return ResponseEntity.ok(cars);
	}

	@GetMapping("/viewByVehicleID")
	public ResponseEntity<?> findCarByVehicleID(@RequestParam(required = false) long carId) {
		if (carId == 0) {
			Map<String, String> errors = new HashMap<>();
			errors.put("vehicleId", "Vehicle ID cannot be blank.");
			return ResponseEntity.badRequest().body(errors);
		}

		Optional<Car> car = carRepository.findById(carId);
		if (car.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No car found with Vehicle ID " + carId);
		}

		return ResponseEntity.ok(car.get());
	}

	@PutMapping("/update/{carId}")
	public ResponseEntity<?> updateCarDetails(@PathVariable int carId, @Valid @RequestBody Car car,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		}

		Car updatedCar = carService.updateCarDetails(carId, car);
		if (updatedCar == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID " + carId + " not found.");
		}

		return new ResponseEntity<>(updatedCar, HttpStatus.OK);
	}

	@DeleteMapping("/delete/{carId}")
	public ResponseEntity<String> deleteCar(@PathVariable int carId) {
		try {
			carService.deleteCarById(carId);
			return ResponseEntity.ok("Car with Vehicle ID " + carId + " deleted successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/{carId}")
	public Car getCarDetails(@PathVariable Long carId) throws InvalidEntityException {
		return carService.getCarDetails(carId);
	}

	@PostMapping("/updateCarAvailability/{carId}/{availability}")
	public void handleCarAvailability(@PathVariable long carId, @PathVariable String availability) {
		Optional<Car> car = carRepository.findById(carId);
		if (car.isPresent()) {
			Car updatedCar = car.get();
			updatedCar.setStatus(availability);
			carRepository.save(updatedCar);
		}
	}

}
