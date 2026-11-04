package com.myfiles.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myfiles.Dtos.LogInResponse;
import com.myfiles.Dtos.LoginRequest;
import com.myfiles.Dtos.ReqResponse;
import com.myfiles.Dtos.UserRegistrationDto;
import com.myfiles.services.Services;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class loginController {
	
	@Autowired
	private Services service;
	
	
	@PostMapping("/login")
	public ResponseEntity<ReqResponse> login(@RequestBody LoginRequest dto) {
		  return ResponseEntity.ok(
	                service.login(dto)
	        );
	}
	
	@PostMapping("/register")
	public String UserRegister(@RequestBody UserRegistrationDto dto) {
		return service.userRegistration(dto);
	}
	
	
	
	

}
