package com.ccd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.ccd.model.UserInfo;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

	private final RestTemplate restTemplate;

	public UserController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private String backendUrl = "http://localhost:9090";

	@GetMapping("/loginPage")
	public String showLoginPage(Model model) {
		model.addAttribute("userInfo", new UserInfo());
		return "login1"; // Return the login page
	}

	@PostMapping("/loginUser")
	public String loginUser(@ModelAttribute UserInfo userInfo, BindingResult result, Model model, HttpSession session) {
		String apiUrl = backendUrl + "/api/users/login";
		try {
			// Send login request to REST API
			UserInfo res = restTemplate.postForObject(apiUrl, userInfo, UserInfo.class);

			if (res != null) {
				// If login is successful, store the role in the session
				session.setAttribute("role", res.getRole());
				session.setAttribute("userObj", res);

				// Redirect based on role
				if ("admin".equalsIgnoreCase(res.getRole())) {
					return "adminDashboard1"; // Redirect to Admin dashboard
				} else if ("employee".equalsIgnoreCase(res.getRole())) {
					return "index1"; // Redirect to Employee dashboard
				} else {
					model.addAttribute("error", "Invalid role assigned!");
					return "login1"; // Redirect back to login with error if role is invalid
				}
			} else {
				model.addAttribute("error", "Invalid credentials!");
				return "login1"; // Redirect to login page if credentials are invalid
			}
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			model.addAttribute("error", "An error occurred during login. Please try again.");
			return "login1";
		}
	}

}
