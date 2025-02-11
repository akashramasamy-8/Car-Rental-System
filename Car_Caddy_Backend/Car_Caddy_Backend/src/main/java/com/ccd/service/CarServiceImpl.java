package com.ccd.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccd.exception.InvalidEntityException;
import com.ccd.exception.NoDataFoundException;
import com.ccd.model.Car;
import com.ccd.repository.CarRepository;

import jakarta.mail.MessagingException;

@Service
public class CarServiceImpl implements CarService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private PdfService pdfService;

	// Check maintenance date for cars every day (or adjust frequency as needed)

	// @Scheduled(cron = "*/120 * * * * *") // Runs every 120 second
	public void checkMaintenanceDates() {
		List<Car> cars = carRepository.findAll();

		for (Car car : cars) {
			if (car.getNextMaintenanceDate() != null) {
				long daysUntilNextMaintenance = ChronoUnit.DAYS.between(LocalDate.now(), car.getNextMaintenanceDate());

				System.out.println(
						"Car ID: " + car.getCarId() + " - Days until next maintenance: " + daysUntilNextMaintenance);

				// Send email alert for maintenance due within the next 7 days (including today)
				if (daysUntilNextMaintenance >= 0 && daysUntilNextMaintenance <= 7) {
					sendMaintenanceAlertEmail(car);
				}
			}
		}
	}

	private void sendMaintenanceAlertEmail(Car car) {
		System.out.println("Preparing to send maintenance alert email for Car ID: " + car.getCarId()); // Log

		String subject = "Upcoming Vehicle Maintenance Reminder";
		String htmlContent = "<html>" + "<body>" + "<h2>Maintenance Reminder</h2>" + "<p>Dear User,</p>"
				+ "<p>This is a reminder that your vehicle with ID <strong>" + car.getCarId()
				+ "</strong> is due for maintenance on <strong>" + car.getNextMaintenanceDate() + "</strong>.</p>"
				+ "<p>Please schedule your maintenance at the earliest.</p>"
				+ "<p>Thank you for using our services!</p>" + "</body>" + "</html>";

		try {
			emailService.sendHtmlEmailWithImage("springboardmentor402.mentor2@gmail.com", subject, htmlContent,
					"images/car_success.jpg", // Replace with your image path in the classpath
					"carImage");
			System.out.println("Maintenance alert email sent successfully!");
		} catch (MessagingException e) {
			System.err.println("Failed to send email: " + e.getMessage());
		}
	}

	public Car addCar(Car car) {
	    // Save the car entity first
	    Car savedCar = carRepository.save(car);

	    // Ensure the email is present
	    if (savedCar.getEmail() != null && !savedCar.getEmail().isEmpty()) {
	        // Prepare email content
	        String htmlContent = "<html>" +
	                "<body>" +
	                "<h2>Vehicle Registration Successful</h2>" +
	                "<p>Dear User,</p>" +
	                "<p>Your vehicle has been added successfully to our system.</p>" +
	                "<p><strong>Vehicle ID:</strong> " + savedCar.getCarId() + "</p>" +
	                "<img src='cid:carImage' style='width:400px;height:auto;'>" +
	                "<p>Thank you for using our services!</p>" +
	                "</body>" +
	                "</html>";

	        try {
	            emailService.sendHtmlEmailWithImage(
	                savedCar.getEmail(), // Send to the registered user's email
	                "Vehicle Registration Successful",
	                htmlContent,
	                "images/car_success.jpg", // Path to the image
	                "carImage" // Content ID for embedding
	            );
	        } catch (MessagingException e) {
	            System.err.println("Failed to send email: " + e.getMessage());
	        }
	    } else {
	        System.err.println("Email is missing, skipping email notification.");
	    }

	    return savedCar;
	}

	public List<Car> getAllCars() {
		List<Car> cars = carRepository.findAll();

		// Generate PDF for car details
		byte[] pdfBytes = pdfService.generateCarDetailsPdf(cars);

		// Prepare email content
		String htmlContent = "<html>" + "<body>" + "<h2>Car Details Report</h2>" + "<p>Dear User,</p>"
				+ "<p>Attached is the PDF containing details of all cars in our system.</p>"
				+ "<p>Thank you for using our services!</p>" + "</body>" + "</html>";

		try {
			emailService.sendHtmlEmailWithAttachment("springboardmentor402.mentor2@gmail.com", "All Car Details Report", htmlContent,
					pdfBytes, "CarDetails.pdf" // Filename for the attachment
			);
		} catch (MessagingException e) {
			System.err.println("Failed to send email with PDF: " + e.getMessage());
		}

		return cars;
	}

	public List<Car> findCarsByStatus(String status) {
		return carRepository.findByStatus(status);
	}

	public List<Car> findCarsByVehicleType(String vehicleType) {
		return carRepository.findByVehicleType(vehicleType);
	}

	public Car updateCarDetails(long carId, Car updatedCar) {
		Car existingCar = carRepository.findById(carId).orElse(null);

		if (existingCar != null) {
			updatedCar.setCarId(carId);
			Car savedCar = carRepository.save(updatedCar);

			// Prepare email content
			String htmlContent = "<html>" + "<body>" + "<h2>Vehicle Update Successful</h2>" + "<p>Dear User,</p>"
					+ "<p>Your vehicle details have been updated successfully.</p>"
					+ "<p><strong>Updated Details:</strong></p>" + "<ul>" + "<li>Vehicle ID: " + savedCar.getCarId()
					+ "</li>" + "<li>Vehicle Type: " + savedCar.getVehicleType() + "</li>" + "<li>Model: "
					+ savedCar.getModel() + "</li>" + "<li>Year of Manufacture: " + savedCar.getYearOfManufacture()
					+ "</li>" + "</ul>" + "<img src='cid:carImage' style='width:400px;height:auto;'>"
					+ "<p>Thank you for using our services!</p>" + "</body>" + "</html>";

			try {
				emailService.sendHtmlEmailWithImage("springboardmentor402.mentor2@gmail.com", "Vehicle Updation Successful",
						htmlContent, "images/car_success.jpg", // Replace with your image path in the classpath
						"carImage");
			} catch (MessagingException e) {
				System.err.println("Failed to send email: " + e.getMessage());
			}

			return savedCar;
		}

		return null;
	}

	public void deleteCarById(long carId) {
		if (carRepository.existsById(carId)) {
			carRepository.deleteById(carId);

			// Prepare email content
			String htmlContent = "<html>" + "<body>" + "<h2>Vehicle Deletion Successful</h2>" + "<p>Dear User,</p>"
					+ "<p>Your vehicle with ID <strong>" + carId + "</strong> has been deleted from our system.</p>"
					+ "<img src='cid:carImage' style='width:400px;height:auto;'>"
					+ "<p>If this action was not intended, please contact our support team immediately.</p>"
					+ "<p>Thank you for using our services!</p>" + "</body>" + "</html>";

			try {
				emailService.sendHtmlEmailWithImage("springboardmentor402.mentor2@gmail.com", "Vehicle Deletion Successful",
						htmlContent, "images/car_success.jpg", // Replace with your image path in the classpath
						"carImage");
			} catch (MessagingException e) {
				System.err.println("Failed to send email: " + e.getMessage());
			}
		} else {
			throw new IllegalArgumentException("Car ID not found: " + carId);
		}
	}

	public Car getCarById(long carId) throws InvalidEntityException {
		return carRepository.findById(carId)
				.orElseThrow(() -> new InvalidEntityException("Car with ID " + carId + " not found."));
	}

	public List<Car> getAllBookings() throws NoDataFoundException {
		List<Car> cars = carRepository.findAll();
		if (cars.isEmpty()) {
			throw new NoDataFoundException("No bookings found.");
		}
		return cars;
	}
	
	 public Car getCarDetails(Long carId) throws InvalidEntityException {
	    	return carRepository.findById(carId).orElseThrow(
	    			() -> new InvalidEntityException("Car with ID " + carId + " not found."));
	    }
	    

}
