package com.contact.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendMail(String to,String subject,String message){
		boolean flag = false;
		
		String from ="12azqr12@gmail.com";
		
		Properties properties = new Properties();
		properties.put("mail.smtp.auth",true);
		properties.put("mail.smtp.starttls.enable",true);
		properties.put("mail.smtp.port","587");
		properties.put("mail.smtp.host","smtp.gmail.com");
		
		
		Session session = Session.getInstance(properties,new Authenticator() {
			
			String userName="ENTER USERNAME";
			String password="ENTER PASSWORD";
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication(userName, password);
			}
		});

		
try {
			
			Message message1 = new MimeMessage(session);
			
			message1.setRecipient(Message.RecipientType.TO,new InternetAddress(to));
			
			message1.setFrom(new InternetAddress(from));
			
			
			//message1.setText(message);
			message1.setContent(message,"text/html");
			
			message1.setSubject(subject);
			
			Transport.send(message1);
			
			System.out.println(message1);
			
			flag =true;
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return flag;
	}
}
