package com.infrastructure.monitoring.config;

import com.infrastructure.monitoring.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/projects/**")
                        .hasAnyRole("OFFICER", "ADMIN", "VIEWER")

                        .requestMatchers(HttpMethod.POST, "/api/projects/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/projects/**")
                        .hasAnyRole("OFFICER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/dashboard/**")
                        .hasAnyRole("OFFICER", "ADMIN", "VIEWER")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}