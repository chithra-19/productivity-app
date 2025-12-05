package com.climbup.security;

import com.climbup.service.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth
                // H2 console — use AntPathRequestMatcher
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()

                // Public endpoints — use MvcRequestMatcher with servlet path inline
                .requestMatchers(new MvcRequestMatcher(introspector, "/auth/register")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/auth/login")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/css/**")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/js/**")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/images/**")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/dashboard")).authenticated()
                .requestMatchers(new MvcRequestMatcher(introspector, "/error")).permitAll()
                .requestMatchers(new MvcRequestMatcher(introspector, "/favicon.ico")).permitAll()

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        // Allow H2 console to render in frames
        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
    
    

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}