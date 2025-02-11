package com.ccd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "admin") // Specify the table name explicitly
public class Admin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@NotEmpty(message = "Provide value for FirstName")
	@Size(max = 20, message = "FirstName must not exceed 20 characters")
	private String firstName;

	@NotNull
	@NotEmpty(message = "Provide value for LastName")
	@Size(max = 20, message = "lastName must not exceed 20 characters")
	private String lastName;

	@NotNull
	@NotEmpty(message = "Provide value for Phonenumber")
	@Size(max = 10, message = "Invalid PhoneNumber please provide valid phoneNumber")
	private String phoneNumber;

	@NotNull(message = "Email should not be null")
	@NotEmpty(message = "Provide value for Email")
	@Email(message = "Email should be valid")
	private String email;

	@NotNull
	@NotEmpty(message = "Provide value for Username")
	@Size(max = 20, message = "username must not exceed 20 characters")
	private String username;

	@NotNull
	@NotEmpty(message = "Provide value for Password")
	private String password;

	// Default Constructor
	public Admin() {
	}

	// Parameterized Constructor
	public Admin(String firstName, String lastName, String username, String phoneNumber, String email,
			String password) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
