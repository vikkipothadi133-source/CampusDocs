package com.myfiles.Util;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Base64;

import com.myfiles.Entity.UserAccount;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class Jwtutil {


	public String tokenGeneration(UserAccount user) {

		return Jwts.builder()
				.subject(user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
				.signWith(getPrivateKey(), Jwts.SIG.RS256)
				.compact();
	}
	
 public Claims validateToken(String token) {

		 return Jwts.parser()
		            .verifyWith(getPublicKey())
		            .build()
		            .parseSignedClaims(token)
		            .getPayload();
	    }
	
	 private PublicKey getPublicKey() {
	        try {
	            String key = new String(
	                    new ClassPathResource("keys/publickey.pem")
	                            .getInputStream()
	                            .readAllBytes(),
	                    StandardCharsets.UTF_8
	            );

	            key = key.replaceAll("-----BEGIN (.*)-----", "")
	                     .replaceAll("-----END (.*)-----", "")
	                     .replaceAll("\\s", "");

	            byte[] decoded = Base64.getDecoder().decode(key);

	            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
	            return KeyFactory.getInstance("RSA").generatePublic(spec);

	        } catch (Exception e) {
	            throw new RuntimeException("Error loading public key", e);
	        }
	    }
	 
	  // ================= PRIVATE KEY =================
	    private PrivateKey getPrivateKey() {
	        try {
	            String key = new String(
	                    new ClassPathResource("keys/privatekey.pem")
	                            .getInputStream()
	                            .readAllBytes(),
	                    StandardCharsets.UTF_8
	            );

	            key = key.replaceAll("-----BEGIN (.*)-----", "")
	                     .replaceAll("-----END (.*)-----", "")
	                     .replaceAll("\\s", "");

	            byte[] decoded = Base64.getDecoder().decode(key);

	            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
	            return KeyFactory.getInstance("RSA").generatePrivate(spec);

	        } catch (Exception e) {
	            throw new RuntimeException("Error loading private key", e);
	        }
	    }

}
