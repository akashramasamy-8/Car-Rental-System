package com.ccd.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

@Entity
@Data
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long employeeId;

	@NotNull
	@NotEmpty(message = "Provide value for employee name")
	private String employeeName;

	@NotNull(message = "Date of birth should not be empty")
	@PastOrPresent(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@NotNull(message = "Email cannot be null")
	@NotEmpty(message = "Email cannot be Empty")
	@Email(message = "Email should be valid")
	private String employeeEmail;

	private String accountType; // temporary or permanent

	private String defaultPassword;

	@NotNull
	@NotEmpty(message = "Provide value for designation")
	private String designation;

//    @FutureOrPresent(message = "AccountExpiryDate date should be either current or future date")
	private LocalDate accountExpiryDate;

	@NotNull
	@NotEmpty(message = "Provide value for status")
	private String status; // active or inactive

	private String availabilityStatus = "available";

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Rent_Booking> bookings;

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

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public List<Rent_Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Rent_Booking> bookings) {
		this.bookings = bookings;
	}

	public Employee(int employeeId, String employeeName, LocalDate dateOfBirth, String employeeEmail,
			String accountType, String defaultPassword, String designation, LocalDate accountExpiryDate, String status,
			List<Rent_Booking> bookings) {// , Car assignedCar
		super();
		this.employeeId = employeeId;
		this.employeeName = employeeName;
		this.dateOfBirth = dateOfBirth;
		this.employeeEmail = employeeEmail;
		this.accountType = accountType;
		this.defaultPassword = defaultPassword;
		this.designation = designation;
		this.accountExpiryDate = accountExpiryDate;
		this.status = status;
		this.bookings = bookings;
	}

	public Employee() {
		super();
	}

}