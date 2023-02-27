package com.contact.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.contact.dao.UserRepositeries;
import com.contact.entities.User;

public class UserDetailsServiceImp implements UserDetailsService{

	@Autowired
	private UserRepositeries userRepositeries;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = this.userRepositeries.getUserByUserName(username);
		//User user = this.userRepositeries.findByEmail(username);
		
		if(user==null){
			
			throw new UsernameNotFoundException("User Not Found....");
		}
		
		
		CustomUserDetails customUserDetails = new CustomUserDetails(user);
		
		return customUserDetails;
	}

}
