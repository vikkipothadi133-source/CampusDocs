package com.example.demo.GatewayRouter;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {
	
	 @Bean
	    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

	        return builder.routes()

	            
	                .route("auth-service", route -> route
	                        .path("/auth/**", "/admin/**")
	                        .uri("lb://AUTHSERVICE")
	                )

	                .route("file-service", route -> route
	                        .path("/files/v1/**")
	                        .uri("lb://MyFiles")
	                )

	                .build();
	    }
}
