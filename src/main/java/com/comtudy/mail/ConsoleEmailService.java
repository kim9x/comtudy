package com.comtudy.mail;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("local")
@Component
public class ConsoleEmailService implements EmailService {

	@Override
	public void sendEmail(EmailMessage emailMessage) {
		// TODO Auto-generated method stub
		log.info("sent email: {}", emailMessage.getMessage());
	}

}
