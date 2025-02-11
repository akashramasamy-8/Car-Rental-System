package com.ccd.controller;

import com.ccd.model.Car;
import com.ccd.model.Maintenance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    private String backendBaseUrl = "http://localhost:9090";
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/create/{carId}")
    public String showAddMaintenanceForm(@PathVariable Long carId, Model model) {
        model.addAttribute("maintenance", new Maintenance());
        model.addAttribute("carId", carId);
        return "addMaintenance5";
    }

    @PostMapping("/create/{carId}")
    public String addMaintenance(@PathVariable Long carId, @ModelAttribute Maintenance maintenance, BindingResult bindingResult, Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/create/" + carId;
            maintenance.setCarId(carId);
            String response = restTemplate.postForObject(url, maintenance, String.class);
            model.addAttribute("message", response);
            return "redirect:/maintenance/all/car/" + carId;
        } catch (HttpClientErrorException e) {
            Map<String, String> errors = extractErrorsFromException(e);
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                if (entry.getKey().equals("error")) {
                    model.addAttribute("error", entry.getValue());
                } else {
                    bindingResult.rejectValue(entry.getKey(), "", entry.getValue());
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error adding maintenance: " + e.getMessage());
        }
        return "addMaintenance5";
    }

    @GetMapping("/edit/{maintenanceId}/car/{carId}")
    public String showEditMaintenanceForm(@PathVariable Long maintenanceId, @PathVariable Long carId, Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/" + maintenanceId;
            Maintenance maintenance = restTemplate.getForObject(url, Maintenance.class);
            model.addAttribute("maintenance", maintenance);
            model.addAttribute("carId", carId);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching maintenance details: " + e.getMessage());
        }
        return "editMaintenance5";
    }

    @PostMapping("/update/{maintenanceId}/car/{carId}")
    public String updateMaintenance(@PathVariable Long maintenanceId, @PathVariable Long carId,
                                    @ModelAttribute Maintenance maintenance, BindingResult bindingResult, Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/update/" + maintenanceId + "/" + carId;
            restTemplate.put(url, maintenance);
            model.addAttribute("message", "Maintenance record updated successfully.");
            return "redirect:/maintenance/all/car/" + carId;
        } catch (HttpClientErrorException e) {
            Map<String, String> errors = extractErrorsFromException(e);
            for (Map.Entry<String, String> entry : errors.entrySet()) {
                if (entry.getKey().equals("error")) {
                    model.addAttribute("error", entry.getValue());
                } else {
                    bindingResult.rejectValue(entry.getKey(), "", entry.getValue());
                }
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error updating maintenance: " + e.getMessage());
        }
        return "editMaintenance5";
    }

    @PostMapping("/delete/{maintenanceId}/car/{carId}")
    public String deleteMaintenance(@PathVariable Long maintenanceId, @PathVariable Long carId, Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/delete/" + maintenanceId;
            restTemplate.delete(url);
            model.addAttribute("message", "Maintenance record deleted successfully.");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting maintenance: " + e.getMessage());
        }
        return "redirect:/maintenance/all/car/" + carId;
    }

    @GetMapping("/all/car/{carId}")
    public String getAllMaintenanceByCar(@PathVariable Long carId, Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/car/" + carId;
            Maintenance[] maintenances = restTemplate.getForObject(url, Maintenance[].class);
            model.addAttribute("maintenances", maintenances);
            model.addAttribute("carId", carId);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching maintenance records: " + e.getMessage());
        }
        return "MaintenanceList5";
    }

    @GetMapping("/all")
    public String getAllMaintenance(@RequestParam(required = false) String status, Model model) {
        try {
            String url = (status != null && !status.isEmpty()) ? backendBaseUrl + "/maintenance/status/" + status : backendBaseUrl + "/maintenance/all";
            Maintenance[] maintenances = restTemplate.getForObject(url, Maintenance[].class);
            model.addAttribute("maintenances", maintenances);
            model.addAttribute("selectedStatus", status);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching maintenance records: " + e.getMessage());
        }
        return "allMaintenanceList5";
    }

    @GetMapping("/MaintenanceHome")
    public String showHomePage() {
        return "home5";
    }

    @GetMapping("/viewAllCar")
    public String getCarMaintenanceDetails(Model model) {
        try {
            String url = backendBaseUrl + "/maintenance/carDetails";
            Object[] carDetails = restTemplate.getForObject(url, Object[].class);
            model.addAttribute("carDetails", carDetails);
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching car maintenance details: " + e.getMessage());
        }
        return "carList5";
    }

    // ✅ Fixed extractErrorsFromException method - moved out of updateMaintenance
    private Map<String, String> extractErrorsFromException(HttpClientErrorException e) {
        Map<String, String> errors = new HashMap<>();
        try {
            String responseBody = e.getResponseBodyAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            errors = objectMapper.readValue(responseBody, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException ex) {
            errors.put("error", "An unexpected error occurred.");
        }
        return errors;
    }
}
