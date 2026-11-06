package com.myfiles.Util;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.myfiles.Entity.UserAccount;
import com.myfiles.services.GoogleAuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	
	@Autowired
	 private GoogleAuthService googleAuthService;



	@Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
		
																							
		
		

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

        String name = oauth2User.getAttribute("name");

        Boolean emailVerified =
                oauth2User.getAttribute("email_verified");
        
        
        if(email == null || !emailVerified.TRUE.equals(emailVerified)) {
        	response.sendError(HttpServletResponse.SC_UNAUTHORIZED , "Google email is not verified !!");
        	return;
        }
       
 
        UserAccount user =
                googleAuthService.findOrCreateGoogleUser(email, name);
        
        String authToken = googleAuthService.generateAuthToken(user);
        
        
        ResponseCookie cookie = ResponseCookie.from("token", authToken)
                .httpOnly(true)
                .secure(false)  // Localhost HTTP only
                .path("/")
                .sameSite("Lax")
                .maxAge(3600)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        
        response.sendRedirect("http://localhost:4200/files");
    }
}
