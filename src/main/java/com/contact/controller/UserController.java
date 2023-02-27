package com.contact.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.contact.dao.ContactRepo;
import com.contact.dao.UserRepositeries;
import com.contact.entities.Contact;
import com.contact.entities.User;
import com.contact.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepositeries userRepositeries;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private ContactRepo contactRepo;

	@ModelAttribute
	public void commonData(Model model, Principal principal) {

		String username = principal.getName();
		User user = this.userRepositeries.getUserByUserName(username);
		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "User Dashboard");
		return "User/user_dashboard";
	}

	// Open add form handller

	@GetMapping("/add-contact")
	public String openAddContactForm(Model m) {
		m.addAttribute("title", "Add Contact");
		m.addAttribute("contact", new Contact());
		return "User/add_contact_form";
	}

	// Proccessing Contact Form

	@PostMapping("/submit-contact")
	public String submitContactForm(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			@RequestParam("profileimage") MultipartFile file, Principal principal, Model m, HttpSession session) {

		if (result.hasErrors()) {
			m.addAttribute("contact", contact);
			return "User/add_contact_form";
		}

		try {
			String username = principal.getName();
			User user = this.userRepositeries.getUserByUserName(username);

			// proccessing upload file

			if (file.isEmpty()) {
				System.out.println(" File Not Uploded....");
				contact.setImage("default.png");
			} else {

				contact.setImage(file.getOriginalFilename());
				File savefile = new ClassPathResource("static/image").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println(" File Uploded Succesfully....");

			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepositeries.save(user);

			System.out.println("Data : " + contact);

			session.setAttribute("message", new Message("New Contact Added..", "alert-success"));

		} catch (Exception e) {
			session.setAttribute("message",
					new Message("Something Went Wrong ! Try Again..." + e.getMessage(), "alert-danger"));

			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
		}
		return "User/add_contact_form";
	}

	// View Contacts Handler

	// per page contact = 3[n]
	// current page =0[current page]

	@GetMapping("/show-contact/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model m, Principal principal) {

		m.addAttribute("title", "View Contacts");

		String username = principal.getName();

		User user = this.userRepositeries.getUserByUserName(username);

		// pagination

		Pageable pageable = PageRequest.of(page, 2);

		Page<Contact> contacts = this.contactRepo.getContactByUserId(user.getId(), pageable);

		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "User/show_contacts";
	}

	// Showing Specific Contact Details

	@RequestMapping("/{cId}/contact/")
	public String showContact(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		Optional<Contact> contactById = contactRepo.findById(cId);
		Contact contact = contactById.get();

		// String contact_name = contact.getName();

		String userName = principal.getName();

		User user = this.userRepositeries.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {

			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "User/contacts_details";
	}

	// Delete Contact Handler

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession sessio) {

		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();

		this.contactRepo.delete(contact);

		System.out.println(cId);
		sessio.setAttribute("message", new Message("Contact Deleted Sucessfully...", "alert-success"));

		return "redirect:/user/show-contact/0";
	}

	// update form handler

	@PostMapping("/update/{cid}")
	public String updateContact(Model model, @PathVariable("cid") Integer cId) {

		model.addAttribute("title", "Update Contact..");

		Contact contact = this.contactRepo.findById(cId).get();
		model.addAttribute("contact", contact);

		return "User/update_form";
	}

	// process update form

	@PostMapping("/process-contact")
	public String updatedContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result,
			Principal principal, Model model, HttpSession session) {

		try {

			if (result.hasErrors()) {
				model.addAttribute("contact", contact);
				return "User/update_form";
			}

			User user = this.userRepositeries.getUserByUserName(principal.getName());
			contact.setUser(user);
			contact.setImage("default.png");
			this.contactRepo.save(contact);
			session.setAttribute("message", new Message(" Contact Updated Successfully..", "alert-success"));

			return "redirect:/user/show-contact/0";
			// return "User/show_contacts";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			session.setAttribute("message",
					new Message("Something Went Wrong ! Try Again..." + e.getMessage(), "alert-danger"));
			return "User/update_form";
		}

	}

	// User Profile Handler
	@GetMapping("/userProfile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile Page");
		return "User/user_profile";
	}

	// Open Settings Handler

	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("title", "Settings Page");
		return "User/settings";
	}

	// Change Password Handler

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpwd") String oldPassword, @RequestParam("newpwd") String newPassword,
			Principal principl, HttpSession session) {

		if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
			User currentUser = this.userRepositeries.getUserByUserName(principl.getName());

			if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {

				// change password
				currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
				currentUser.setCheckbox(true);
				this.userRepositeries.save(currentUser);
				
				  session.setAttribute("message",new
				  Message("Password Changed Successfully..","alert-success"));
				 

			} else {
				
				  session.setAttribute("message",new
				  Message("Please Enter Correct old password", "alert-danger"));
				 
				return "redirect:/user/settings";
			}

			return "redirect:/user/index";
		}
		
		
		 
		System.out.println("Plz enter old and new passsword..");
		 session.setAttribute("message",new
				  Message("Please Enter Correct old password and New Password", "alert-danger"));
		return "redirect:/user/settings";

	}

}
