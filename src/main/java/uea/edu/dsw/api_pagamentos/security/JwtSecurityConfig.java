package uea.edu.dsw.api_pagamentos.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@Profile("jwt")
@EnableWebSecurity
@EnableMethodSecurity
public class JwtSecurityConfig {
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final String corsAllowedOrigins;
        private final boolean docsPublicEnabled;
        private final boolean h2ConsoleEnabled;

        public JwtSecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        AuthenticationProvider authenticationProvider,
                        @Value("${app.security.cors.allowed-origins:http://localhost:4200}") String corsAllowedOrigins,
                        @Value("${app.security.docs-public-enabled:false}") boolean docsPublicEnabled,
                        @Value("${app.security.h2-console-enabled:false}") boolean h2ConsoleEnabled) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.authenticationProvider = authenticationProvider;
                this.corsAllowedOrigins = corsAllowedOrigins;
                this.docsPublicEnabled = docsPublicEnabled;
                this.h2ConsoleEnabled = h2ConsoleEnabled;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable());

                if (h2ConsoleEnabled) {
                        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
                }

                http.authorizeHttpRequests(auth -> {
                        if (docsPublicEnabled) {
                                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html/**",
                                                "/v3/api-docs.yaml", "/swagger-resources/**")
                                                .permitAll();
                        }

                        if (h2ConsoleEnabled) {
                                auth.requestMatchers("/h2-console/**").permitAll();
                        }

                        auth.requestMatchers("/api/auth/login").permitAll()
                                        .anyRequest().authenticated();
                }).sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public UrlBasedCorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                List<String> allowedOrigins = Arrays.stream(corsAllowedOrigins.split(","))
                                .map(String::trim)
                                .filter(origin -> !origin.isEmpty())
                                .toList();
                configuration.setAllowedOrigins(allowedOrigins);
                configuration.addAllowedMethod("*");
                configuration.addAllowedHeader("*");
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}
