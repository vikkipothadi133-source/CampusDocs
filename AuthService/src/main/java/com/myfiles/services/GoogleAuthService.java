package com.myfiles.services;

import java.util.Optional;
import java.util.UUID;

import com.myfiles.Entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.myfiles.Dtos.ReqResponse;
import com.myfiles.Entity.UserAccount;
import com.myfiles.Util.Jwtutil;
import com.myfiles.repository.UserRepository;

@Service
public class GoogleAuthService {
    
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Jwtutil jwtutil;
	
	
	

	

	public UserAccount findOrCreateGoogleUser(String email,String name) {

			Optional<UserAccount> existingUser = userRepository.findByEmail(email);
			
			if(existingUser.isPresent()) {
		           System.out.println("Existing Google user found");
		            return existingUser.get();
			}
			 UserAccount newUser = new UserAccount();

		        newUser.setFullName(name);
		        newUser.setEmail(email);

		        // Generate a random password for Google users
		        String randomPassword = UUID.randomUUID().toString();

		        // Store only the BCrypt hash
		        newUser.setPassword(passwordEncoder.encode(randomPassword));

		        newUser.setAuthProvider("GOOGLE");
				newUser.setRole(Role.EMPLOYEE);




		        return userRepository.save(newUser);
	}
	
	
	public String generateAuthToken(UserAccount user) {
		return jwtutil.tokenGeneration(user);
	}

}
