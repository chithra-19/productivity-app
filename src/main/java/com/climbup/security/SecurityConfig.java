package com.climbup.security;

import com.climbup.service.user.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Value("${app.remember-me-key:}")
    private String rememberMeKey;

    // Use the key from the environment; if it is missing, use a random one
    // (login still works, but "remember me" will not survive a restart)
    private String resolveRememberMeKey() {
        return (rememberMeKey == null || rememberMeKey.isBlank())
                ? UUID.randomUUID().toString()
                : rememberMeKey;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
    	http
    	

            .authenticationProvider(authProvider())
            .authorizeHttpRequests(auth -> auth

            	    // Auth & public pages
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/register")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/login")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/perform_login")).permitAll()

            	    // Password reset (IMPORTANT)
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/forgot-password")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/reset-password")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/auth/reset-password/**")).permitAll()

            	    // Static resources
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/css/**")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/js/**")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/images/**")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/favicon.ico")).permitAll()
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/error")).permitAll()

            	    // Dashboard
            	    .requestMatchers(new MvcRequestMatcher(introspector, "/dashboard/**")).authenticated()

            	    // Everything else
            	    .anyRequest().authenticated()
            	)


            .formLogin(form -> form
            	    .loginPage("/auth/login")
            	    .loginProcessingUrl("/perform_login")
            	    .passwordParameter("password")
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

            .rememberMe(remember -> remember
                .key(resolveRememberMeKey())
                .tokenValiditySeconds(14 * 24 * 60 * 60)   // 14 days
                .userDetailsService(userDetailsService)
            )

            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

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
    
    
