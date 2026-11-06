package com.myfiles.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	@Email(message = "Enter a valid email")
	private String email;

	@Column(nullable = false)
	private String password;


//	@Column(nullable = false)
	private String authProvider = "LOCAL";

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

}
