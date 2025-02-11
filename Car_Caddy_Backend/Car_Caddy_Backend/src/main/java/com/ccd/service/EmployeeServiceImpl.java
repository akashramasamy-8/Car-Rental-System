package com.ccd.service;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Employee;
import com.ccd.repository.EmployeeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmailService emailService;

	public Employee getEmployeeById(long id) throws InvalidEntityException {
		return employeeRepository.findById(id)
				.orElseThrow(() -> new InvalidEntityException("Employee with ID " + id + " not found."));
	}

	public Employee addEmployee(Employee employee) {
		// Set default status if not provided
		System.out.println(employee.getAccountExpiryDate());
		try {
			if (employee.getStatus() == null || employee.getStatus().isEmpty()) {
				employee.setStatus("active");
			}
			employee.setAvailabilityStatus("available");

			// Generate default password based on account type, DOB, and name length
			if (employee.getDefaultPassword() == null || employee.getDefaultPassword().isEmpty()) {
				String accountTypePrefix = "P"; // Default to Permanent
				if ("temporary".equalsIgnoreCase(employee.getAccountType())) {
					accountTypePrefix = "T";
				}

				// Extract DOB and convert to YYYYMMDD format
				LocalDate dob = employee.getDateOfBirth();
				String dobFormatted = dob != null ? dob.toString().replaceAll("-", "") : "00000000";

				// Calculate the length of the employee name
				int nameLength = employee.getEmployeeName() != null ? employee.getEmployeeName().length() : 0;

				// Construct the password
				String defaultPassword = accountTypePrefix + dobFormatted + nameLength;
				employee.setDefaultPassword(defaultPassword);
			}

			// Set account date automatically for 'temporary' account type
			if ("temporary".equalsIgnoreCase(employee.getAccountType())) {
//		            LocalDate expiryDate = LocalDate.now().plusYears(1); 
				LocalDate expiryDate = employee.getAccountExpiryDate();
				employee.setAccountExpiryDate(expiryDate);
			}
			System.out.println("Default Password: " + employee.getDefaultPassword());

			// Save the employee to the database
		} catch (Exception e) {
			System.out.println(e);
		}
		return employeeRepository.save(employee);
	}

	public boolean replacePasswordIfValid(long empId, String defaultPassword, String newPassword) {
		// Find the employee by ID
		Optional<Employee> employeeOptional = employeeRepository.findById(empId);

		if (employeeOptional.isPresent()) {
			Employee employee = employeeOptional.get();

			// Check if the provided default password matches the one in the database
			if (employee.getDefaultPassword().equals(defaultPassword)) {
				// Update the password to the new password
				employee.setDefaultPassword(newPassword);
				employeeRepository.save(employee); // Save the updated employee
				String loginDate = LocalDate.now().toString();

				String subject = "Employee Login Successful - " + employee.getEmployeeName() + " (ID: "
						+ employee.getEmployeeId() + ")";
				String emailBody = String.format("Dear Admin,\n\n"
						+ "We would like to inform you that the following employee has successfully logged into the system:\n\n"
						+ "Employee Name: %s\n" + "Employee ID: %d\n" + "Login Date: %s\n\n"
						+ "If you did not authorize this login, please take appropriate action.\n\n" + "Best regards,\n"
						+ "CarCaddy System", employee.getEmployeeName(), employee.getEmployeeId(), loginDate);

				// Send email
				emailService.sendEmail("rugwed8181@gmail.com", subject, emailBody);
				return true;
			}
		}

		// Return false if the employee doesn't exist or the passwords don't match
		return false;
	}

	public List<Employee> getAllEmployees() {
		// Fetch all employees from the database and return the list
		return employeeRepository.findAll();
	}

	public boolean deactivateEmployee(long employeeId) {
		Optional<Employee> employeeOptional = employeeRepository.findById(employeeId);
		if (employeeOptional.isPresent()) {
			Employee employee = employeeOptional.get();
			List<Employee> employeeList = Collections.singletonList(employee);

			// Check if the employee is active before deactivating
			if ("Active".equalsIgnoreCase(employee.getStatus())) {
				employee.setStatus("inactive");
				employeeRepository.save(employee);

				emailService.sendEmployeeDeactivationEmail(employee);
				emailService.sendAdminNotification(employeeList);
				return true;
			}
		}
		return false;
	}

	// Deactivate employees whose expiryDate has passed
//			@Transactional
//		    public int deactivateExpiredEmployees() {
//		        LocalDate currentDate = LocalDate.now(); // Get today's date
//		        return employeeRepository.deactivateExpiredEmployees(currentDate); // Call the repository method
//		    }

	@Transactional
	public long deactivateExpiredEmployees() {
		LocalDate currentDate = LocalDate.now(); // Get today's date
		List<Employee> employeesToDeactivate = employeeRepository.findActiveEmployeesWithExpiredAccounts(currentDate);
		long deactivatedCount = employeeRepository.deactivateExpiredEmployees(currentDate); // Deactivate employees
		for (Employee employee : employeesToDeactivate) {
			emailService.sendEmployeeDeactivationEmail(employee);
		}
		emailService.sendAdminNotification(employeesToDeactivate);
		return deactivatedCount;

	}

}