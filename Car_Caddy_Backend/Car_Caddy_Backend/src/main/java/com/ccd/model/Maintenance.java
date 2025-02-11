package com.ccd.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "maintenance")
public class Maintenance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long maintenanceId;

	@NotBlank(message = "This field cannot be empty")
	private String defectType;

	@NotBlank(message = "This field cannot be empty")
	private String defectDescription;

	@PastOrPresent(message = "This field value should be either past or current date")
	private LocalDate dateReceivedForMaintenance;

	@FutureOrPresent(message = "This field value should be either current or future date")
	private LocalDate expectedDeliveryDate;

	@Positive(message = "Maintenance cost should be greater than zero")
	private Double maintenanceCost;

	@NotBlank(message = "This field cannot be empty")
	private String maintenanceStatus;

	@ManyToOne
	@JoinColumn(name = "carId")
	@JsonIgnoreProperties("maintenance")
	private Car car;

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

	public Car getCar() {
		return car;
	}
   
	public void setCar(Car car) {
		this.car = car;
	}
	 @JsonProperty("carId")
	    public Long getCarId() {
	        return car != null ? car.getCarId() : null;
	    }
	 private String message; 

	    public String getMessage() {
	        return message;
	    }

	    public void setMessage(String message) {
	        this.message = message;
	    }

}
