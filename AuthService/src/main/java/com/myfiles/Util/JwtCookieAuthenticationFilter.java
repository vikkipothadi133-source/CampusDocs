package com.myfiles.Util;




import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {

    private final Jwtutil jwtutil;

    public JwtCookieAuthenticationFilter(Jwtutil jwtutil) {
        this.jwtutil = jwtutil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

     
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

     
        if (token != null && !token.isBlank()) {

            try {
                Claims claims = jwtutil.validateToken(token);

                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                if (email == null || email.isBlank()
                        || role == null || role.isBlank()) {
                    throw new IllegalArgumentException("Invalid JWT claims");
                }

               
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority("ROLE_" + role)
                                )
                        );

               
                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {

                SecurityContextHolder.clearContext();

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid or expired token"
                );
                return;
            }
        }

    
        filterChain.doFilter(request, response);
    }
}

