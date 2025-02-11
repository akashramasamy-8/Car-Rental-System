package com.ccd.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class Car {

	private long carId;
	private String vehicleType;
	private String model;
	private int yearOfManufacture;
	private String licencePlateNumber;
	private String registrationCertificate;
	private String fuelType;
	private double mileage;
	private double startKm;
	private double endKm;
	private double rentRate;
	private String status;
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private LocalDate lastMaintenanceDate;
	private LocalDate nextMaintenanceDate;
	private List<Rent_Booking> bookings;

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
//	public void setEmp(Employee emp) {
//		this.emp = emp;
//	}

}
