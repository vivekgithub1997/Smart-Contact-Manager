package com.contact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.Contact;
import com.contact.entities.User;


public interface ContactRepo extends JpaRepository<Contact, Integer>{
	
	//pagination
	
	//Current Page 
	//Contact Per Page-3

	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> getContactByUserId(@Param("userId") int userId,Pageable pageable);
	
	// Searching Query
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
	
}
