package com.contact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.contact.dao.ContactRepo;
import com.contact.dao.UserRepositeries;
import com.contact.entities.Contact;
import com.contact.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepositeries userRepositeries;
	
	@Autowired
	private ContactRepo contactRepo;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		System.out.println(query);
		String name = principal.getName();
		System.out.println(name);
		
		User user = this.userRepositeries.getUserByUserName(principal.getName());
		List<Contact> contacts= this.contactRepo.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contacts);
		
	}
}
