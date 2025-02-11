package com.ccd.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ccd.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Optional<Employee> findByEmployeeEmail(String email);

	@Modifying
	@Transactional

	@Query("UPDATE Employee e SET e.status = 'inactive' WHERE e.accountExpiryDate < :currentDate AND e.status = 'active'")
	long deactivateExpiredEmployees(@Param("currentDate") LocalDate currentDate);

	Optional<List<Employee>> findAllByStatus(String status);

	List<Employee> findByEmployeeNameContainingIgnoreCase(String name);

	@Query("SELECT e FROM Employee e WHERE e.accountExpiryDate < :currentDate AND e.status = 'active'")
	List<Employee> findActiveEmployeesWithExpiredAccounts(@Param("currentDate") LocalDate currentDate);

}
