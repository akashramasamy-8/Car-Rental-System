package com.ccd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ccd.model.Admin;

import java.util.Optional;

/*
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByUsername(String username);
    Optional<Admin> findByEmail(String email);
}*/
@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> { // Correct generic type
	Optional<Admin> findByUsername(String username);

	Optional<Admin> findByEmail(String email);
}
