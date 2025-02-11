package com.ccd.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Column(name = "first_name", length = 50, nullable = false)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	@Column(name = "last_name", length = 50, nullable = false)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Column(name = "email", unique = true, nullable = false)
	private String email;

	@NotBlank(message = "Mobile number is required")
	@Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
	@Column(name = "mobile", unique = true, nullable = false, length = 10)
	private String mobile;

	@NotBlank(message = "Gender is required")
	@Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
	@Column(name = "gender", length = 10, nullable = false)
	private String gender;

	@Min(value = 1, message = "Loyalty points must be between 1 and 10")
	@Max(value = 10, message = "Loyalty points must be between 1 and 10")
	@Column(name = "loyalty_points", nullable = false)
	private int loyaltyPoints = 0;

	@NotNull(message = "Blocklist status must be either 'true' or 'false'")
	@Column(name = "blocklist_status", nullable = false)
	private boolean blocklistStatus = false;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters long")
	@Column(name = "password", nullable = false)
	private String password;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<Rent_Booking> bookings;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int loyaltyPoints) {
		this.loyaltyPoints = loyaltyPoints;
	}

	public boolean getBlocklistStatus() {
		return blocklistStatus;
	}

	public void setBlocklistStatus(boolean blocklistStatus) {
		this.blocklistStatus = blocklistStatus;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Rent_Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Rent_Booking> bookings) {
		this.bookings = bookings;
	}

	public Customer(int id,
			@NotBlank(message = "First name is required") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
			@NotBlank(message = "Last name is required") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
			@NotBlank(message = "Email is required") @Email(message = "Please provide a valid email address") String email,
			@NotBlank(message = "Mobile number is required") @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobile,
			@NotBlank(message = "Gender is required") @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other") String gender,
			@Min(value = 1, message = "Loyalty points must be between 1 and 10") @Max(value = 10, message = "Loyalty points must be between 1 and 10") int loyaltyPoints,
			@NotNull(message = "Blocklist status must be either 'true' or 'false'") boolean blocklistStatus,
			@NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters long") String password,
			List<Rent_Booking> bookings) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.loyaltyPoints = loyaltyPoints;
		this.blocklistStatus = blocklistStatus;
		this.password = password;
		this.bookings = bookings;
	}

	public Customer() {
		super();
		// TODO Auto-generated constructor stub
	}

}
