package com.ccd.service;

import java.util.List;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.InvalidFieldException;
import com.ccd.exception.NoDataFoundException;
import com.ccd.model.Car;

public interface CarService {

	public Car getCarById(long id) throws InvalidEntityException;

	public List<Car> getAllBookings() throws NoDataFoundException;

	void checkMaintenanceDates(); // Method for checking car maintenance dates

	public Car addCar(Car car) ; // Method for adding a new car

	List<Car> getAllCars(); // Method for retrieving all cars

	List<Car> findCarsByStatus(String status); // Method for finding cars by their status

	List<Car> findCarsByVehicleType(String vehicleType); // Method for finding cars by vehicle type

	Car updateCarDetails(long vehicleId, Car updatedCar); // Method for updating car details

	void deleteCarById(long vehicleId); // Method for deleting a car by its ID

	public Car getCarDetails(Long carId) throws InvalidEntityException;

}
