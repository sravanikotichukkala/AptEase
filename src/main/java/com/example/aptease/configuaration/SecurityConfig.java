package com.example.aptease.configuaration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.example.aptease.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/index", "/css/**", "/js/**", "/images/**").permitAll() // Allow index page
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/resident/**").hasRole("RESIDENT")
                .requestMatchers("/delivery/**").hasRole("DELIVERY_AGENT")
                .anyRequest().authenticated()
        )
                .formLogin(form -> form
                	    .loginPage("/login")
                	    .permitAll()
                	    .successHandler((request, response, authentication) -> {
                	        String role = authentication.getAuthorities().iterator().next().getAuthority();
                	        if (role.equals("ROLE_ADMIN")) {
                	            response.sendRedirect("/admin/dashboard");
                	        } else if (role.equals("ROLE_RESIDENT")) {
                	            response.sendRedirect("/resident/dashboard");
                	        } else if (role.equals("ROLE_DELIVERY_AGENT")) {
                	            response.sendRedirect("/delivery/dashboard");
                	        } else {
                	            response.sendRedirect("/login?error");
                	        }
                	    })
                	)
                .logout(logout -> logout
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder) // Use injected encoder
                .and()
                .build();
    }
}
