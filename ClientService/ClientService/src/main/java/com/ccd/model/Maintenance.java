package com.ccd.model;
import java.time.LocalDate;


import org.springframework.format.annotation.DateTimeFormat;

public class Maintenance {
    private Long maintenanceId;
    private String defectType;
    private String defectDescription;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateReceivedForMaintenance;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedDeliveryDate;
    private Double maintenanceCost;
    private String maintenanceStatus;
    private Long carId; // To store the associated car ID
	public Long getMaintenanceId() {
		return maintenanceId;
	}
	public void setMaintenanceId(Long maintenanceId) {
		this.maintenanceId = maintenanceId;
	}
	public String getDefectType() {
		return defectType;
	}
	public void setDefectType(String defectType) {
		this.defectType = defectType;
	}
	public String getDefectDescription() {
		return defectDescription;
	}
	public void setDefectDescription(String defectDescription) {
		this.defectDescription = defectDescription;
	}
	public LocalDate getDateReceivedForMaintenance() {
		return dateReceivedForMaintenance;
	}
	public void setDateReceivedForMaintenance(LocalDate dateReceivedForMaintenance) {
		this.dateReceivedForMaintenance = dateReceivedForMaintenance;
	}
	public LocalDate getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}
	public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}
	public Double getMaintenanceCost() {
		return maintenanceCost;
	}
	public void setMaintenanceCost(Double maintenanceCost) {
		this.maintenanceCost = maintenanceCost;
	}
	public String getMaintenanceStatus() {
		return maintenanceStatus;
	}
	public void setMaintenanceStatus(String maintenanceStatus) {
		this.maintenanceStatus = maintenanceStatus;
	}
	public Long getCarId() {
		return carId;
	}
	public void setCarId(Long carId) {
		this.carId = carId;
	}
	
	    private String message;

	    // Getter method
	    public String getMessage() {
	        return message;
	    }

	    // Setter method
	    public void setMessage(String message) {
	        this.message = message;
	    }
	}


    // Getters and Setters
