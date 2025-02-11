package com.ccd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccd.model.Role;
import com.ccd.model.User;
import com.ccd.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public boolean registerAdmin(String username, String password) {
		if (userRepository.findByUsername(username) != null) {
			return false; // Username already exists
		}
		User admin = new User();
		admin.setUsername(username);
		admin.setPassword(password);
		admin.setRole(Role.ADMIN);
		userRepository.save(admin);
		return true;
	}

	public boolean loginAsAdmin(String username, String password) {
		User admin = userRepository.findByUsername(username);
		return admin != null && admin.getRole() == Role.ADMIN && admin.getPassword().equals(password);
	}

	public List<User> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users;
	}

	// User registration
	public User registerUser(String username, String password) {
		if (userRepository.findByUsername(username) != null) {
			throw new IllegalArgumentException("Username already exists.");
		}

		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setRole(Role.USER);

		return userRepository.save(user);
	}

	// User login
	public boolean loginAsUser(String username, String password) {
		User user = userRepository.findByUsername(username);
		return user != null && user.getPassword().equals(password);
	}
}
