package com.ccd.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Rent_Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long bookingId;

	@ManyToOne
	@JoinColumn(name = "car_id")
	private Car car;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@FutureOrPresent(message = "Booking Date Should Be Either Current Or Future Date")
	@NotNull(message = "Start Can't Be Empty")
	private LocalDate startDate;

	@Future(message = "Booking End Date Should Be Future Date")
	@NotNull(message = "End Date Can't Be Empty")
	private LocalDate endDate;

	private double totalFare;

	@NotNull(message = "Discount Can't Be Empty")
	@Min(value = 1, message = "Discount Must Be At least 1")
	@Max(value = 10, message = "Discount Must Be At Most 10")
	private int discount;

	@NotEmpty(message = "Location Can't Be Empty")
	private String location;

	@NotEmpty(message = "Status Can't Be Empty")
	private String status;

	private int days;

	public long getBookingId() {
		return bookingId;
	}

	public void setBookingId(long bookingId) {
		this.bookingId = bookingId;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public double getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(double totalFare) {
		this.totalFare = totalFare;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Rent_Booking(int bookingId, Car car, Employee employee, Customer customer,
			@FutureOrPresent(message = "Booking Date Should Be Either Current Or Future Date") @NotNull(message = "Start Can't Be Empty") LocalDate startDate,
			@Future(message = "Booking End Date Should Be Future Date") @NotNull(message = "End Date Can't Be Empty") LocalDate endDate,
			double totalFare,
			@NotNull(message = "Discount Can't Be Empty") @Min(value = 1, message = "Discount Must Be At least 1") @Max(value = 10, message = "Discount Must Be At Most 10") int discount,
			@NotEmpty(message = "Location Can't Be Empty") String location,
			@NotEmpty(message = "Status Can't Be Empty") String status, int days) {
		super();
		this.bookingId = bookingId;
		this.car = car;
		this.employee = employee;
		this.customer = customer;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalFare = totalFare;
		this.discount = discount;
		this.location = location;
		this.status = status;
		this.days = days;
	}

	public Rent_Booking() {
		super();
	}

}