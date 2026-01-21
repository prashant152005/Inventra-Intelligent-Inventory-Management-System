package com.inventra.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // 1. Preflight - FIRST priority
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 2. Public resources
                        .requestMatchers(
                                "/",
                                "/index.html", "/login.html", "/forgotpassword.html", "/reset-password.html",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        .requestMatchers("/auth/**").permitAll()

                        // 3. ADMIN-only WRITE (POST/DELETE) - specific BEFORE general
                        .requestMatchers(HttpMethod.POST, "/products/add").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/delete/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/*/stock-in").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/*/stock-out").hasAuthority("ROLE_ADMIN")

                        // 4. Explicitly allow ALL GET on /products/** (this fixes /all and /low-stock)
                        .requestMatchers(HttpMethod.GET, "/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // 5. General fallback for other /products/**
                        .requestMatchers("/products/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_EMPLOYEE")

                        // 6. Everything else authenticated
                        .anyRequest().authenticated()
                )

                // CRITICAL: Force JSON responses for ALL errors (403, 401, etc.)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((req, res, authEx) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\": \"Unauthorized - Please login\"}");
                        })
                        .accessDeniedHandler((req, res, accessDeniedEx) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"error\": \"Forbidden - Insufficient permissions\"}");
                        })
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}