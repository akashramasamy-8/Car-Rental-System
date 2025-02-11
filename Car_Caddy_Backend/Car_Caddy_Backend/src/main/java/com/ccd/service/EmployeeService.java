package com.ccd.service;

import java.util.List;

import com.ccd.exception.InvalidEntityException;
import com.ccd.model.Employee;

public interface EmployeeService {

	public Employee getEmployeeById(long id) throws InvalidEntityException;

	public Employee addEmployee(Employee employee);

	public boolean replacePasswordIfValid(long empId, String defaultPassword, String newPassword);

	public List<Employee> getAllEmployees();

	public boolean deactivateEmployee(long employeeId);

	public long deactivateExpiredEmployees();
}
