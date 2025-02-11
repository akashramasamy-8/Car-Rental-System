package com.ccd.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class Employee {

	private long employeeId;
	private String employeeName;
	private LocalDate dateOfBirth;
	private String employeeEmail;
	private String accountType; // temporary or permanent
	private String defaultPassword;
	private String designation;
	private LocalDate accountExpiryDate;
	private String status; // active or inactive
	private String availabilityStatus = "available";
	private List<Rent_Booking> bookings;

//    private Car assignedCar;

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmployeeEmail() {
		return employeeEmail;
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public LocalDate getAccountExpiryDate() {
		return accountExpiryDate;
	}

	public void setAccountExpiryDate(LocalDate accountExpiryDate) {
		this.accountExpiryDate = accountExpiryDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Rent_Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Rent_Booking> bookings) {
		this.bookings = bookings;
	}

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}
	
	
	
//	public Car getAssignedCar() {
//		return assignedCar;
//	}
//	public void setAssignedCar(Car assignedCar) {
//		this.assignedCar = assignedCar;
//	}
//    

}
