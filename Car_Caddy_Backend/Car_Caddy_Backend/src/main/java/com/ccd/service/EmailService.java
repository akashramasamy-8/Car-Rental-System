package com.ccd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ccd.model.Employee;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
		System.out.println("Email sent successfully to " + to);
	}

	public void sendHtmlEmailWithImage(String to, String subject, String htmlContent, String imagePath, String imageCid)
			throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true); // Enable HTML content

		// Attach image as inline content
		if (imagePath != null && imageCid != null) {
			ClassPathResource imageResource = new ClassPathResource(imagePath); // Ensure the image is in the classpath
			helper.addInline(imageCid, imageResource);
		}

		mailSender.send(message);
		System.out.println("HTML email sent successfully to " + to);
	}

	// Email with attachment (PDF)
	public void sendHtmlEmailWithAttachment(String to, String subject, String htmlContent, byte[] attachmentBytes,
			String attachmentFilename) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true); // Enable HTML content

		// Attach PDF as an attachment
		if (attachmentBytes != null && attachmentFilename != null) {
			ByteArrayResource resource = new ByteArrayResource(attachmentBytes);
			helper.addAttachment(attachmentFilename, resource);
		}

		mailSender.send(message);
		System.out.println("HTML email with attachment sent successfully to " + to);
	}

	public void sendEmployeeDeactivationEmail(Employee employee) {
		String subject = "Account Deactivation Notice";
		String body = String.format(
				"Dear %s,\n\nYour account has been deactivated by the admin. Please contact support for more details.\n\nRegards,\nAdmin Team",
				employee.getEmployeeName());

		sendEmail(employee.getEmployeeEmail(), subject, body);
	}

	public void sendAdminNotification(List<Employee> deactivatedEmployees) {
		String subject = "Employee Deactivation Summary";
		StringBuilder body = new StringBuilder("The following employees were deactivated:\n");

		for (Employee employee : deactivatedEmployees) {
			body.append(String.format("- %s (Email: %s)\n", employee.getEmployeeName(), employee.getEmployeeEmail()));
		}

		sendEmail("nivethas.21msc@kongu.edu", subject, body.toString());
		
		
	}
	
	
	//Maintenance 
	public void sendMaintenanceAddEmail(String to, String subject, String body) 
	{
	    sendEmail(to, subject, body);
	}
	
	public void sendMaintenanceUpdateEmail(String to, String subject, String body) {
	    sendEmail(to, subject, body);
	}
	public void sendMaintenanceCompletionEmail(String to, String subject, String body) {

	    sendEmail(to, subject, body);
	}
	 public void sendDailyReportEmail( String subject, String body) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo("springboardmentor430@gmail.com");
	        message.setSubject(subject);
	        message.setText(body);
	        mailSender.send(message);
	        System.out.println("Daily report email sent");
	    }
    


}