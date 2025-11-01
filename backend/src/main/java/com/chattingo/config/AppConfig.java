package com.chattingo.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class AppConfig {

    private final JwtValidator jwtValidator;

    @Value("${cors.allowed.origins:http://localhost:3000,http://localhost}")
    private String allowedOrigins;

    @Value("${cors.allowed.methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    public AppConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login", "/signup").permitAll()  // public endpoints
                .requestMatchers("/api/**").authenticated()       // protected endpoints
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtValidator, BasicAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration cfg = new CorsConfiguration();

            String[] origins = allowedOrigins.split(",");
            cfg.setAllowedOriginPatterns(Arrays.asList(origins));

            String[] methods = allowedMethods.split(",");
            cfg.setAllowedMethods(Arrays.asList(methods));

            cfg.setAllowedHeaders(Collections.singletonList("*"));
            cfg.setExposedHeaders(Collections.singletonList("Authorization"));
            cfg.setAllowCredentials(true);
            cfg.setMaxAge(3600L);

            return cfg;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
