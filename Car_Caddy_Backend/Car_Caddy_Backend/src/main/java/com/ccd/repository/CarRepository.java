package com.ccd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ccd.model.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
	List<Car> findByStatus(String status);

	List<Car> findByVehicleType(String type);

	List<Car> findByRentRateBetween(double minRate, double maxRate);
	
	//added
	@Query("SELECT c.email FROM Car c WHERE c.carId = :carId")
	String findOwnerEmailByCarId(@Param("carId") Long carId);
}
