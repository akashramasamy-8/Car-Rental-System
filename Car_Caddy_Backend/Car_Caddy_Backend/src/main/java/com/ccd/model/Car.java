package com.ccd.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
public class Car {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long carId;

	@NotBlank(message = "Vehicle type is required")
	private String vehicleType;
	
	@NotNull(message = "Email cannot be null")
	@NotEmpty(message = "Email cannot be Empty")
	@Email(message = "Email should be valid")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@NotBlank(message = "Model is required")
	private String model;

	@NotNull(message = "Year of manufacture is required")
	@Min(value = 1900, message = "Year must be valid")
	private Integer yearOfManufacture;

	@Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{2}[0-9]{4}[A-Z]{2}$", message = "Invalid license plate number")
	private String licencePlateNumber;

	@NotBlank(message = "Registration certificate is required")
	private String registrationCertificate;

	@NotBlank(message = "Fuel type is required")
	private String fuelType;

	@NotNull(message = "Mileage is required")
	@Positive(message = "Mileage must be positive")
	private Double mileage;

	@NotNull(message = "Start KM is required")
	@Positive(message = "Start KM must be positive")
	private Double startKm;

	@NotNull(message = "End KM is required")
	@Positive(message = "End KM must be positive")
	private Double endKm;

	@NotNull(message = "Rent rate is required")
	@Positive(message = "Rent rate must be positive")
	private Double rentRate;

	@NotBlank(message = "Status is required")
	private String status;

	@PastOrPresent(message = "Last maintenance date must be either past date or present date")
	private LocalDate lastMaintenanceDate;
	@FutureOrPresent(message = "Last maintenance date must be either future date or present date")
	private LocalDate nextMaintenanceDate;

	@OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Rent_Booking> bookings;

	@OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
	@JsonIgnoreProperties("car")
	private List<Maintenance> maintenance;

//    @OneToOne(mappedBy = "assignedCar")
//    @JsonIgnore
//    private Employee emp;

	public long getCarId() {
		return carId;
	}

	public void setCarId(long carId) {
		this.carId = carId;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getYearOfManufacture() {
		return yearOfManufacture;
	}

	public void setYearOfManufacture(int yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}

	public String getLicencePlateNumber() {
		return licencePlateNumber;
	}

	public void setLicencePlateNumber(String licencePlateNumber) {
		this.licencePlateNumber = licencePlateNumber;
	}

	public String getRegistrationCertificate() {
		return registrationCertificate;
	}

	public void setRegistrationCertificate(String registrationCertificate) {
		this.registrationCertificate = registrationCertificate;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public double getMileage() {
		return mileage;
	}

	public void setMileage(double mileage) {
		this.mileage = mileage;
	}

	public double getStartKm() {
		return startKm;
	}

	public void setStartKm(double startKm) {
		this.startKm = startKm;
	}

	public double getEndKm() {
		return endKm;
	}

	public void setEndKm(double endKm) {
		this.endKm = endKm;
	}

	public double getRentRate() {
		return rentRate;
	}

	public void setRentRate(double rentRate) {
		this.rentRate = rentRate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getLastMaintenanceDate() {
		return lastMaintenanceDate;
	}

	public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
		this.lastMaintenanceDate = lastMaintenanceDate;
	}

	public LocalDate getNextMaintenanceDate() {
		return nextMaintenanceDate;
	}

	public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
		this.nextMaintenanceDate = nextMaintenanceDate;
	}

	public List<Rent_Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Rent_Booking> bookings) {
		this.bookings = bookings;
	}

//	public Employee getEmp() {
//		return emp;
//	}
//
//	public void setEmp(Employee emp) {
//		this.emp = emp;
//	}

	public Car(int carId, String vehicleType, String model, int yearOfManufacture, String licencePlateNumber,
			String registrationCertificate, String fuelType, double mileage, double startKm, double endKm,
			double rentRate, String status, LocalDate lastMaintenanceDate, LocalDate nextMaintenanceDate,
			List<Rent_Booking> bookings) { // , Employee emp
		super();
		this.carId = carId;
		this.vehicleType = vehicleType;
		this.model = model;
		this.yearOfManufacture = yearOfManufacture;
		this.licencePlateNumber = licencePlateNumber;
		this.registrationCertificate = registrationCertificate;
		this.fuelType = fuelType;
		this.mileage = mileage;
		this.startKm = startKm;
		this.endKm = endKm;
		this.rentRate = rentRate;
		this.status = status;
		this.lastMaintenanceDate = lastMaintenanceDate;
		this.nextMaintenanceDate = nextMaintenanceDate;
		this.bookings = bookings;
//		this.emp = emp;
	}

	public Car() {
		super();
		// TODO Auto-generated constructor stub
	}

}
