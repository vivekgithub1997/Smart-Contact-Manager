package com.contact.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.entities.User;

public interface UserRepositeries extends JpaRepository<User, Integer>{

	
	  @Query("select u from User u where u.email =:email") 
	  public User getUserByUserName(@Param("email") String email);
	 
	
	//public User findByEmail(String email);
		
	
}
