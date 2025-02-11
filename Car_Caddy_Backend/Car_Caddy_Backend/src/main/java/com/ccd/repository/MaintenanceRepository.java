package com.ccd.repository;

import com.ccd.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

	Optional<Maintenance> findById(Long maintenanceId);

	List<Maintenance> findByMaintenanceStatus(String status);

	long countByCar_CarId(Long carId);

	long countByDateReceivedForMaintenance(@Param("date") LocalDate date);

	long countCarsByExpectedDeliveryDate(@Param("expectedDeliveryDate") LocalDate expectedDeliveryDate);

	@Query("SELECT m.car.carId, COUNT(m) FROM Maintenance m GROUP BY m.car.carId")
	List<Object[]> getCarMaintenanceCounts();

	List<Maintenance> findAll();

	List<Maintenance> findByCar_CarId(Long carId);
	
	@Query("SELECT c.carId, c.vehicleType, c.model, c.fuelType, c.mileage, c.licencePlateNumber, c.lastMaintenanceDate, c.nextMaintenanceDate, COUNT(m.maintenanceId) " +
	           "FROM Car c LEFT JOIN c.maintenance m " +
	           "GROUP BY c.carId, c.vehicleType, c.model, c.fuelType, c.mileage, c.licencePlateNumber, c.lastMaintenanceDate, c.nextMaintenanceDate")
	    List<Object[]> getCarMaintenanceDetails();
	

}
