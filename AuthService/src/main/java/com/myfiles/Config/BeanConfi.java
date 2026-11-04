package com.myfiles.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
public class BeanConfi {

	
	@Bean
	public PasswordEncoder passenoderEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	  @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationSuccessHandler successHandler)
	            throws Exception {

	        http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth ->
	                auth.anyRequest().permitAll()
	            )
	            .formLogin(form -> form.disable())
	            .httpBasic(basic -> basic.disable())
	            .oauth2Login(oauth -> oauth
	                    .successHandler(successHandler)
	                    );;

	        return http.build();
	    }
	  
}
