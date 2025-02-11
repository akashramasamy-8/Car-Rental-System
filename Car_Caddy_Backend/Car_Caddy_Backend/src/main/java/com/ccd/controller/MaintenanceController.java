package com.ccd.controller;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Maintenance;
import com.ccd.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @GetMapping("/{maintenanceId}")
    public Maintenance getMaintenanceById(@PathVariable Long maintenanceId) throws InvalidEntityException {
        return maintenanceService.getMaintenanceById(maintenanceId);
    }

    @GetMapping("/status/{status}")
    public List<Maintenance> getAllMaintenanceByStatus(@PathVariable String status) {
        return maintenanceService.getAllMaintenanceByStatus(status);
    }

    @GetMapping("/maintenanceCountByCar/{carId}")
    public long getMaintenanceCountByCarId(@PathVariable Long carId) {
        return maintenanceService.getMaintenanceCountByCarId(carId);
    }

    @GetMapping("/maintenance-counts")
    public String getCarMaintenanceCounts() {
        return maintenanceService.getFormattedCarMaintenanceCounts();
    }

    @GetMapping("/count/{date}")
    public long getMaintenanceCountByDateReceived(@PathVariable("date") String date) {
        LocalDate localDate = LocalDate.parse(date);
        return maintenanceService.getMaintenanceCountByDateReceived(localDate);
    }

    @GetMapping("/count1/{expectedDeliveryDate}")
    public long getCountByExpectedDeliveryDate(@PathVariable("expectedDeliveryDate") String expectedDeliveryDate) {
        LocalDate localDate = LocalDate.parse(expectedDeliveryDate);
        return maintenanceService.getCountByExpectedDeliveryDate(localDate);
    }

    @GetMapping("/all")
    public List<Maintenance> getAllMaintenance() {
        return maintenanceService.getAllMaintenance();
    }
    @PostMapping("/create/{carId}")
    public ResponseEntity<String> addMaintenance(@PathVariable Long carId, @RequestBody Maintenance maintenance) throws InvalidEntityException {
        // Call service layer to handle the logic
        String result = maintenanceService.addMaintenance(maintenance, carId);
        return ResponseEntity.ok(result);
    }
    

    @PutMapping("/update/{maintenanceId}/{carId}")
    public ResponseEntity<Maintenance> updateMaintenance(
            @PathVariable Long maintenanceId,
            @PathVariable Long carId,
            @RequestBody Maintenance maintenance) throws InvalidEntityException {
        
      
        return ResponseEntity.ok(maintenanceService.updateMaintenance(maintenanceId, carId, maintenance));
    }
    @GetMapping("/car/{carId}")
    public List<Maintenance> getMaintenanceByCarId(@PathVariable Long carId) {
        return maintenanceService.getMaintenanceByCarId(carId);
    }
    
    @GetMapping("/carDetails")
    public List<Object[]> getCarMaintenanceDetails() {
        return maintenanceService.getCarMaintenanceDetails();
    }
  

    @DeleteMapping("/delete/{maintenanceId}")
    public ResponseEntity<String> deleteMaintenanceById(@PathVariable Long maintenanceId) {
        try {
            maintenanceService.deleteMaintenanceById(maintenanceId);
            return ResponseEntity.ok("Maintenance record deleted successfully.");
        } catch (InvalidEntityException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + e.getMessage());
        }
    }

}


