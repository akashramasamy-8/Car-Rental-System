package com.ccd.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = "com.ccd.model")
@ComponentScan(basePackages = { "com.ccd.repository", "com.ccd.controller", "com.ccd.entities", "com.ccd.service",
		"com.ccd.exception" })
@EnableJpaRepositories(basePackages = "com.ccd.repository")
@EnableScheduling
@SpringBootApplication
public class CarCaddyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarCaddyBackendApplication.class, args);
	}

}
