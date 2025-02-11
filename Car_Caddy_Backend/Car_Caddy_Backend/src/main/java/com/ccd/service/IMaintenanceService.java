package com.ccd.service;

import com.ccd.model.Maintenance;
import com.ccd.exception.InvalidEntityException;
import java.time.LocalDate;
import java.util.List;

public interface IMaintenanceService {
	public String addMaintenance(Maintenance maintenance, Long carId) throws InvalidEntityException;

	public Maintenance updateMaintenance(Long maintenanceId, Long carId, Maintenance maintenance)
			throws InvalidEntityException;

	public Maintenance getMaintenanceById(Long maintenanceId) throws InvalidEntityException;

	public List<Maintenance> getAllMaintenanceByStatus(String status);

	public long getMaintenanceCountByCarId(Long carId);

	public long getMaintenanceCountByDateReceived(LocalDate date);

	public long getCountByExpectedDeliveryDate(LocalDate expectedDeliveryDate);

	public List<Maintenance> getAllMaintenance();

	public String getFormattedCarMaintenanceCounts();

	List<Maintenance> getMaintenanceByCarId(Long carId);

	void deleteMaintenanceById(Long maintenanceId) throws InvalidEntityException;
	List<Object[]> getCarMaintenanceDetails();

	void notifyMaintenanceCompletion(Long maintenanceId) throws InvalidEntityException;

}
