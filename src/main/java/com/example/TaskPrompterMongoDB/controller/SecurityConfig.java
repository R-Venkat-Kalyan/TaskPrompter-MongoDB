package com.example.TaskPrompterMongoDB.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf().disable()
	        .authorizeHttpRequests(auth -> auth
//	            .requestMatchers("/addTask", "/saveTask", "/tasks", "/editTasks", "/deleteTask/{id}", "/updateTask/{id}", "/updateTask", "/profile", "/updateProfile", "/update")
//	            .authenticated()  // Require authentication for specific routes
	            .requestMatchers("/userLogin", "/user","/signin", "/register", "/oauth2/**").permitAll()  // Allow access to login-related routes
	            .anyRequest().permitAll()  // Allow all other requests without authentication
	        )
	        .formLogin(form -> form
	            .loginPage("/signin")
	            .loginProcessingUrl("/userLogin")  // Ensure it matches your controller's @PostMapping
	            .defaultSuccessUrl("/user")
	            .failureUrl("/signin?error")  // Redirect back to /signin on failure
	            .permitAll()
	        )
	        .oauth2Login(oauth2 -> oauth2
	            .loginPage("/signin")  
	            .defaultSuccessUrl("/userSignin")  // Redirect after successful login
	        )
	        .logout(logout -> logout
	            .logoutUrl("/logout")  // URL to trigger the logout process
	            .logoutSuccessUrl("/signin")  // Redirect to login page after successful logout
	            .invalidateHttpSession(true)  // Invalidate the HTTP session to clear user data
	            .deleteCookies("JSESSIONID")  // Remove the JSESSIONID cookie
	        );

	    return http.build();
	}

}

