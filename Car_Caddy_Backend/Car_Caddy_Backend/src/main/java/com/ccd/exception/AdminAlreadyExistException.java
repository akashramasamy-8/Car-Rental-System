package com.ccd.exception;

public class AdminAlreadyExistException extends RuntimeException {
	public AdminAlreadyExistException(String message) {
		super(message);
	}
}
