package com.contact.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.dao.UserRepositeries;
import com.contact.entities.User;
import com.contact.service.EmailService;

@Controller
public class ForgotController {

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepositeries userRepositeries;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//email id form Open Handler
	
	Random random = new Random();
	
	@GetMapping("/forgot")
	public String openEmailForm(){
		return"forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session) {
		
		int otp = random.nextInt(9999);
		if(otp<999){
			System.out.println(otp);
			otp+=1000;
		}
		
		String subject="OTP From Online Contact Mangaer";
		String msg=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP : "
				+ "<b>"+otp
				+ "</b>"
				+ "</h1>"
				+ "</div>";
				
		String to=email;
		
		boolean sendMail = this.emailService.sendMail(to, subject, msg);
		
		if(sendMail) {
			
			System.out.println(sendMail);
			session.setAttribute("myotp",otp);
			session.setAttribute("email",email);
			return "verify_otp";
			
		}
		else {
			System.out.println(sendMail);
			session.setAttribute("message","Check Your Email Id...");
		    return"redirect:/forgot";
		}
		
	}
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session){
		
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		
		if(myOtp==otp){
			
			User user = this.userRepositeries.getUserByUserName(email);
			
			if(user==null) {
				// send error message
				session.setAttribute("message","user does not exit with that email id..");
				return"forgot_email_form";
			}
			else {
				
				//password change form
				
				
				
				return"password_change";
			}
			
			
		}
		else {
			
			session.setAttribute("message","You Enter Wrong OTP..");
			return"verify_otp";
		}
		
		
	}
	
	// Set New Changed Password
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword
			,HttpSession session){
		
		String email = (String)session.getAttribute("email");
		
		User user = this.userRepositeries.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		user.setCheckbox(true);
		this.userRepositeries.save(user);
		
		return "redirect:/signin?change=password Changed Successfully..";
	}
	
	
}
