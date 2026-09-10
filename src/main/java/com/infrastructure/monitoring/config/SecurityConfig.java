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
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Admin officer management endpoints
                        .requestMatchers(HttpMethod.GET, "/api/auth/officers", "/api/users/officers").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/invite-officer", "/api/users/invite-officer").hasRole("ADMIN")

                        // Authenticated user endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/change-password").authenticated()

                        // Public auth endpoints
                        .requestMatchers("/api/auth/**").permitAll()

                        // Ministries
                        .requestMatchers(HttpMethod.GET, "/api/ministries/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/ministries/**").hasRole("ADMIN")

                        // Project view access (All roles)
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").hasAnyRole("OFFICER", "ADMIN", "VIEWER")

                        // Sub-resources: Officer or Admin can report progress or refresh risk
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/milestones", "/api/projects/*/financials", "/api/projects/*/risk/refresh")
                        .hasAnyRole("OFFICER", "ADMIN")

                        // ML Risk Prediction: Officer or Admin only (Viewer read-only, no ML trigger)
                        .requestMatchers(HttpMethod.POST, "/api/projects/*/risk-prediction")
                        .hasAnyRole("OFFICER", "ADMIN")

                        // Project creation & deletion: Admin only
                        .requestMatchers(HttpMethod.POST, "/api/projects").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/projects/**").hasAnyRole("OFFICER", "ADMIN")

                        // Alerts: Officer or Admin only (Viewer cannot see/resolve internal alerts)
                        .requestMatchers(HttpMethod.GET, "/api/alerts/**").hasAnyRole("OFFICER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/alerts/*/resolve").hasAnyRole("OFFICER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/alerts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/alerts/**").hasRole("ADMIN")

                        // Dashboard overview
                        .requestMatchers("/api/dashboard/**").hasAnyRole("OFFICER", "ADMIN", "VIEWER")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}