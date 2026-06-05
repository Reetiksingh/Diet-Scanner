package com.nutrilens.achievement.config;

import com.nutrilens.common.security.HeaderUserFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(new HeaderUserFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

