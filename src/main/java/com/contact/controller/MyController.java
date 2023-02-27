package com.contact.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.contact.dao.UserRepositeries;
import com.contact.entities.User;
import com.contact.helper.Message;

@Controller
public class MyController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepositeries userRepositeries;
	
	@RequestMapping("/")
	public String home(Model m) {
		m.addAttribute("title","Home-Online_Contact_Management");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title","AboutUs-Online_Contact_Management");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title","Signup-Online_Contact_Management");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	//  Custom Login page 
	
	@GetMapping("/signin")
	public String userLogin(Model m) {
		m.addAttribute("title","Login-Online_Contact_Management");
		return "loginn";
	}
	

	
	@PostMapping("/do_registerd")
	public String process(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value = "checkbox",defaultValue ="false") boolean checkbox,
			Model m,HttpSession session)
	{
	
		try {
			
			if(!checkbox) {
				
				System.out.println(" plz Accepte tm  "+checkbox+" "+user);
			    throw new Exception("Please Accept Terms And Conditions...");
				
				
			}
			
			if(result.hasErrors()){
				
				System.out.println(result.toString());
				m.addAttribute("user",user);
				 return "signup";
				
			}
			
			   user.setEnable(true);
			   user.setRole("ROLE_USER");
			   user.setImageUrl("default.png");
			   user.setPassword(passwordEncoder.encode(user.getPassword()));
			   
			   User save = this.userRepositeries.save(user);
			   System.out.println(" Accepted tm  "+checkbox+" "+save);
			   m.addAttribute("user",new User());	
			   session.setAttribute("message",new Message("Successfully Registered....","alert-success"));
			   return "signup";
		}
		catch(Exception e)
		{
			m.addAttribute("user",user);
			e.printStackTrace();
			session.setAttribute("message",new Message("Something Went Wrong"+e.getMessage(), "alert-danger"));
			return "signup";
		}
		
		
		
	}
	
}
