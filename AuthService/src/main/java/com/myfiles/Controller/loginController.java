package com.myfiles.Controller;

import com.myfiles.Dtos.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.myfiles.services.Services;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class loginController {
	
	@Autowired
	private Services service;
	
	
	@PostMapping("/login")
	public ResponseEntity<ReqResponse> login(@RequestBody LoginRequest dto ,HttpServletResponse response ) {
		  return ResponseEntity.ok(
	                service.login(dto, response)
	        );
	}
	
	@PostMapping("/register")
	public String UserRegister(@RequestBody UserRegistrationDto dto) {
		return service.userRegistration(dto);
	}


//	@PreAuthorize("hasRole('SUPER_ADMIN')")
//	@PutMapping("/users")
//	public String updateRole(@RequestBody RoleUpdateDto dto) {
//		return service.updateRole(dto);
//	}
	

}
