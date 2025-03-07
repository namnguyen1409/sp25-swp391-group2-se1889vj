package com.group2.sp25swp391group2se1889vj.config;

import com.group2.sp25swp391group2se1889vj.filter.JwtAuthenticationFilter;
import com.group2.sp25swp391group2se1889vj.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(
                csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        ).authorizeHttpRequests(
                authz -> authz
                        .requestMatchers(new LoggingRequestMatcher()).permitAll()
                        .requestMatchers(
                                "/error/**",
                                "/register/**",
                                "/login/**",
                                "/images/**",
                                "/css/**",
                                "/js/**",
                                "/custom-login/**",
                                "/logout/**",
                                "/forgot-password",
                                "/reset-password"
                        ).permitAll()
                        .anyRequest().authenticated()
        ).formLogin(AbstractHttpConfigurer::disable
        ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
        ).exceptionHandling(e -> e.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        return http.build();
    }
}
