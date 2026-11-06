package com.myfiles.Config;

import com.myfiles.Util.JwtCookieAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity
public class BeanConfi {

	@Autowired
	private JwtCookieAuthenticationFilter jwtFilter;

	
	@Bean
	public PasswordEncoder passenoderEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	  @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationSuccessHandler successHandler)
	            throws Exception {

		  http
				  .csrf(csrf -> csrf.disable())

				  .authorizeHttpRequests(auth -> auth

						  .requestMatchers(
								  "/auth/login",
								  "/oauth2/**",
								  "/login/oauth2/**",
								  "/auth/**",
								  "/auth/oauth2/**"
						  ).permitAll()

						  .requestMatchers("/admin/**")
						  .hasRole("SUPER_ADMIN")

						  .anyRequest().permitAll()
				  )

				  // ADD EXCEPTION HANDLING HERE
				  .exceptionHandling(exception -> exception

						  .authenticationEntryPoint((request, response, ex) -> {
							  response.setStatus(401);
							  response.setContentType("application/json");
							  response.getWriter().write(
									  "{\"message\":\"Please login first\"}"
							  );
						  })

						  .accessDeniedHandler((request, response, ex) -> {
							  response.setStatus(403);
							  response.setContentType("application/json");
							  response.getWriter().write(
									  "{\"message\":\"You don't have admin permissions\"}"
							  );
						  })
				  )

				  .formLogin(form -> form.disable())

				  .httpBasic(basic -> basic.disable())

				  .oauth2Login(oauth -> oauth
						  .successHandler(successHandler)
				  )

				  .addFilterBefore(
						  jwtFilter,
						  UsernamePasswordAuthenticationFilter.class
				  );

	        return http.build();
	    }
	  
}
