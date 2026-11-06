package com.myfiles.services;

import com.myfiles.Dtos.*;
import com.myfiles.Entity.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
		user.setRole(Role.EMPLOYEE);
		
		repo.save(user);
		
		return "User Register Successfull ";
		
		
	}
	
	public ReqResponse login (LoginRequest dto, HttpServletResponse httpResponse) {

		UserAccount user =
				repo.findByEmail(dto.getEmail()).orElseThrow(() -> new ResponseStatusException(
								HttpStatus.UNAUTHORIZED,
								"Invalid email or password"
						)
				);

		boolean match = passwordencoder.matches(dto.getPassword(), user.getPassword());

		if (!match) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Invalid email or password"
			);
		}

		String token = util.tokenGeneration(user);


		ResponseCookie cookie = ResponseCookie.from("token", token)
				.httpOnly(true)
				.secure(false) // Local development with HTTP
				.path("/")
				.sameSite("Lax")
				.maxAge(3600)
				.build();


		httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());


		ReqResponse reqResponse = new ReqResponse();
		reqResponse.setMessage("Login successful");
		reqResponse.setEmail(user.getEmail());


		return reqResponse;
	}

	public String updateRole(RoleUpdateDto dto) {
		System.out.println("User ID: " + dto.getId());
		System.out.println("New Role: " + dto.getRole());

		UserAccount user = repo.findById(dto.getId())
				.orElseThrow(() ->
						new RuntimeException("User not found"));

		user.setRole(dto.getRole());

		repo.save(user);

		return "User role updated successfully";
	}
	

}
