package com.myfiles.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.myfiles.Dtos.LogInResponse;
import com.myfiles.Dtos.LoginRequest;
import com.myfiles.Dtos.RegRes;
import com.myfiles.Dtos.ReqResponse;
import com.myfiles.Dtos.UserRegistrationDto;
import com.myfiles.Entity.UserAccount;
import com.myfiles.Util.Jwtutil;
import com.myfiles.repository.UserRepository;

@Service
public class Services {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private PasswordEncoder passwordencoder;
	
	@Autowired
	private Jwtutil util;
	
	public String userRegistration(UserRegistrationDto dto) {
		if(repo.existsByEmail(dto.getEmail())) {
			throw new RuntimeException("Email already exists !!");
		}
		
		UserAccount user = new UserAccount();
		user.setFullName(dto.getFullName());
		user.setEmail(dto.getEmail());
		user.setPassword(passwordencoder.encode(dto.getPassword()));
		
		repo.save(user);
		
		return "User Register Successfull ";
		
		
	}
	
	public ReqResponse login (LoginRequest dto) {
		
		UserAccount user =
				repo.findByEmail(dto.getEmail()).orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                )
        );
		
		boolean match = passwordencoder.matches(dto.getPassword(), user.getPassword());
		
		if(!match) {
			 throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED,
		                "Invalid email or password"
		        );
		}
		
		String token = util.tokenGeneration(user.getEmail());
		
		ReqResponse response = new ReqResponse();
		response.setAccessToken(token);
		response.setFullName(user.getFullName());
		response.setEmail(user.getEmail());
		response.setUserId(user.getId());
		response.setTokenType("Bearer");
		return response;
		
		
		
	}
	

}
