package com.ccd.service;

import com.ccd.model.Car;
import com.ccd.model.Maintenance;
import com.ccd.exception.InvalidEntityException;
import com.ccd.repository.CarRepository;
import com.ccd.repository.MaintenanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService implements IMaintenanceService {

	 @Autowired
	    private MaintenanceRepository maintenanceRepository;
	    
	    @Autowired 
	    private CarRepository carRepository;
	    
	    private final EmailService emailService;

	    @Autowired
	    public MaintenanceService(EmailService emailService) {
	        this.emailService = emailService;
	    }

	    @Override
	    public String addMaintenance(Maintenance maintenance, Long carId) throws InvalidEntityException {
	        if (maintenance == null) {
	            throw new InvalidEntityException("Maintenance details cannot be null.");
	        }

	        if (maintenance.getDefectType() == null || maintenance.getDefectType().isEmpty()) {
	            throw new InvalidEntityException("Defect type cannot be empty.");
	        }

	        Car car = new Car();
	        car.setCarId(carId);
	        maintenance.setCar(car);

	        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
	        String ownerEmail = carRepository.findOwnerEmailByCarId(carId);
	        if (ownerEmail != null) {
	            System.out.println("Owner Email: " + ownerEmail);
	        } else {
	            System.out.println("Owner email not found for Car ID: " + carId);
	        }
	        
	        emailService.sendMaintenanceAddEmail(
	            ownerEmail, 
	            "Your maintenance request for Car ID " + car.getCarId() + " has been successfully added!", 
	            "Dear Customer,\n\n" +
	            "We have received your maintenance request for your car (Model: " + car.getModel() + 
	            ", License Plate: " + car.getLicencePlateNumber() + ").\n\n" +
	            "Maintenance Details:\n" + maintenance.getDefectType() + " - " + maintenance.getMaintenanceStatus() + "\n\n" +
	            "Rest assured, our team will take care of it and ensure that the necessary maintenance is completed promptly.\n\n" +
	            "If you have any questions, feel free to reach out.\n\n" +
	            "Best Regards,\nMaintenance Team"
	        );

	        return "Maintenance added successfully with ID: " + savedMaintenance.getMaintenanceId();
	    }

	    @Override
	    public Maintenance updateMaintenance(Long maintenanceId, Long carId, Maintenance maintenance) throws InvalidEntityException {
	        Maintenance existingMaintenance = maintenanceRepository.findById(maintenanceId)
	                .orElseThrow(() -> new InvalidEntityException("Maintenance record not found."));

	        Car car = carRepository.findById(carId)
	                .orElseThrow(() -> new InvalidEntityException("Car not found with ID: " + carId));
	        existingMaintenance.setCar(car);

	       
	        String previousStatus = existingMaintenance.getMaintenanceStatus();

	       
	        existingMaintenance.setDefectType(maintenance.getDefectType());
	        existingMaintenance.setDefectDescription(maintenance.getDefectDescription());
	        existingMaintenance.setDateReceivedForMaintenance(maintenance.getDateReceivedForMaintenance());
	        existingMaintenance.setExpectedDeliveryDate(maintenance.getExpectedDeliveryDate());
	        existingMaintenance.setMaintenanceCost(maintenance.getMaintenanceCost());
	        existingMaintenance.setMaintenanceStatus(maintenance.getMaintenanceStatus());

	        Maintenance updatedMaintenance = maintenanceRepository.save(existingMaintenance);

	        String ownerEmail = carRepository.findOwnerEmailByCarId(carId);
	        if (ownerEmail != null) {
	            System.out.println("Owner Email: " + ownerEmail);

	            
	            if (!"Completed".equalsIgnoreCase(previousStatus) && "Completed".equalsIgnoreCase(maintenance.getMaintenanceStatus())) {
	                notifyMaintenanceCompletion(maintenanceId);
	            } else {
	               
	                String emailBody = String.format(
	                    "Dear Customer,\n\n"
	                    + "Your maintenance details for your car (Model: %s, License Plate: %s) have been updated.\n\n"
	                    + "Updated Maintenance Details:\n"
	                    + "Defect Type: %s\n"
	                    + "Defect Description: %s\n"
	                    + "Received Date: %s\n"
	                    + "Expected Delivery Date: %s\n"
	                    + "Maintenance Cost: %.2f\n"
	                    + "Status: %s\n\n"
	                    + "Our team is actively working on it. We will notify you once it's completed.\n\n"
	                    + "Best Regards,\nMaintenance Team",
	                    car.getModel(),
	                    car.getLicencePlateNumber(),
	                    maintenance.getDefectType(),
	                    maintenance.getDefectDescription(),
	                    maintenance.getDateReceivedForMaintenance(),
	                    maintenance.getExpectedDeliveryDate(),
	                    maintenance.getMaintenanceCost(),
	                    maintenance.getMaintenanceStatus()
	                );

	                emailService.sendMaintenanceUpdateEmail(
	                    ownerEmail,
	                    "Maintenance update for Car ID " + car.getCarId(),
	                    emailBody
	                );
	            }
	        } else {
	            System.out.println("Owner email not found for Car ID: " + carId);
	        }

	        return updatedMaintenance;
	    }

	    @Override
	    public Maintenance getMaintenanceById(Long maintenanceId) throws InvalidEntityException {
	        if (maintenanceId == null) {
	            throw new InvalidEntityException("Maintenance ID cannot be null.");
	        }

	        return maintenanceRepository.findById(maintenanceId)
	                .orElseThrow(() -> new InvalidEntityException("Maintenance not found with ID: " + maintenanceId));
	    }

	    @Override
	    public List<Maintenance> getAllMaintenanceByStatus(String status) {
	        return maintenanceRepository.findByMaintenanceStatus(status);
	    }

	    @Override
	    public long getMaintenanceCountByCarId(Long carId) {
	        return maintenanceRepository.countByCar_CarId(carId);
	    }

	    @Override
	    public long getMaintenanceCountByDateReceived(LocalDate date) {
	        return maintenanceRepository.countByDateReceivedForMaintenance(date);
	    }

	    @Override
	    public long getCountByExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
	        return maintenanceRepository.countCarsByExpectedDeliveryDate(expectedDeliveryDate);
	    }

	    @Override
	    public List<Maintenance> getAllMaintenance() {
	        return maintenanceRepository.findAll();
	    }

	    @Override
	    public String getFormattedCarMaintenanceCounts() {
	        List<Object[]> rawCounts = maintenanceRepository.getCarMaintenanceCounts();
	        StringBuilder result = new StringBuilder();

	        for (Object[] entry : rawCounts) {
	            Long carId = (Long) entry[0];
	            Long count = (Long) entry[1];
	            result.append("Car ID: ").append(carId).append(", Count: ").append(count).append("\n");
	        }

	        return result.toString();
	    }
	    
	    @Override
	    public List<Maintenance> getMaintenanceByCarId(Long carId) {
	        return maintenanceRepository.findByCar_CarId(carId);
	    }
	    @Override
	    public void deleteMaintenanceById(Long maintenanceId) throws InvalidEntityException {
	        // Check if the maintenance record exists
	        if (!maintenanceRepository.existsById(maintenanceId)) {
	            throw new InvalidEntityException("Maintenance record not found with ID: " + maintenanceId);
	        }
	       
	        maintenanceRepository.deleteById(maintenanceId);
	    }
	    
	    
	    @Override
	    public List<Object[]> getCarMaintenanceDetails() {
	        return maintenanceRepository.getCarMaintenanceDetails();
	    }
	    @Override
	    public void notifyMaintenanceCompletion(Long maintenanceId) throws InvalidEntityException {
	        
	        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
	                .orElseThrow(() -> new InvalidEntityException("Maintenance record not found."));

	        
	        if (!"Completed".equalsIgnoreCase(maintenance.getMaintenanceStatus())) {
	            throw new InvalidEntityException("Maintenance is not yet completed.");
	        }

	      
	        Car car = maintenance.getCar();
	        if (car == null) {
	            throw new InvalidEntityException("Associated car not found.");
	        }

	       
	        String ownerEmail = carRepository.findOwnerEmailByCarId(car.getCarId());
	        if (ownerEmail != null) {
	            System.out.println("Sending completion email to: " + ownerEmail);

	            
	            String emailBody = String.format(
	               
	                 "We are pleased to inform you that the maintenance for your car (Model: %s, License Plate: %s) has been successfully completed.\n\n"
	                + "Maintenance Details:\n"
	                + "Defect Type: %s\n"
	                + "Defect Description: %s\n"
	                + "Received Date: %s\n"
	                + "Expected Delivery Date: %s\n"
	                + "Maintenance Cost: %.2f\n"
	                + "Final Status: %s\n\n"
	                + "Your vehicle is now ready for collection at your earliest convenience.\n\n"
	                + "Best Regards,\nMaintenance Team",
	                car.getModel(),
	                car.getLicencePlateNumber(),
	                maintenance.getDefectType(),
	                maintenance.getDefectDescription(),
	                maintenance.getDateReceivedForMaintenance(),
	                maintenance.getExpectedDeliveryDate(),
	                maintenance.getMaintenanceCost(),
	                maintenance.getMaintenanceStatus()
	            );

	            // Trigger email notification
	            emailService.sendMaintenanceCompletionEmail(
	                ownerEmail,
	                "Maintenance Completed - Car ID " + car.getCarId(),
	                emailBody
	            );
	        } else {
	            System.out.println("Owner email not found for Car ID: " + car.getCarId());
	        }
	    }
	    
	    @Scheduled(cron = "0 0 8 * * ?")  
	    public void sendDailyDeliveryReport() {
	        LocalDate today = LocalDate.now();
	        LocalDate tomorrow = today.plusDays(1);

	        long carsToBeDeliveredToday = maintenanceRepository.countCarsByExpectedDeliveryDate(today);
	        long carsToBeDeliveredTomorrow = maintenanceRepository.countCarsByExpectedDeliveryDate(tomorrow);

	        String subject = "Daily Car Delivery Report - " + today;
	        String emailBody = String.format(
	            "Dear Admin,\n\n"
	            + "Here is the daily maintenance delivery report:\n\n"
	            + "🚗 Cars scheduled for delivery today (%s): %d\n"
	            + "🚗 Cars scheduled for delivery tomorrow (%s): %d\n\n"
	            + "Please ensure smooth handovers.\n\n"
	            + "Best Regards,\nMaintenance Team",
	            today, carsToBeDeliveredToday, tomorrow, carsToBeDeliveredTomorrow
	        );

	        
	        emailService.sendDailyReportEmail( subject, emailBody);
	    }
	}





//	        if (maintenance.getDefectDescription() == null || maintenance.getDefectDescription().isEmpty()) {
//	            throw new InvalidEntityException("Defect description cannot be empty.");
//	        }
//
//	        if (maintenance.getMaintenanceCost() <= 0) {
//	            throw new InvalidEntityException("Maintenance cost must be greater than zero.");
//	        }
//	        if (maintenance.getDateReceivedForMaintenance() == null || maintenance.getDateReceivedForMaintenance().isAfter(LocalDate.now())) {
//	            throw new InvalidEntityException("Date received for maintenance must be today or in the past.");
//	        }
//	        if (maintenance.getExpectedDeliveryDate().isBefore(LocalDate.now())) {
//	            throw new InvalidEntityException("Expected delivery date must be today or in the future.");
//	        }