package com.ghtk.auction.service.impl;

import com.ghtk.auction.exception.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {
	
	@Autowired

	private JavaMailSender mailSender;
	
	@Async
	public void sendOtpEmail(String to, String otp) {

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject("Auction System OTP");
			message.setText("Your OTP code is: " + otp);
			mailSender.send(message);
		} catch (Exception e) {
			throw new EmailException("OTP service unavailable", e);
		}
	}
	@Async
	public void sendDefaultPassword(String to, String password) {
		
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject("Auction System Password");
			message.setText("Your new password is: " + password);
			mailSender.send(message);
		} catch (Exception e) {
			throw new EmailException("OTP service unavailable", e);
		}
	}
}
