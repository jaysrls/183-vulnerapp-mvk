package ch.bbw.m183.vulnerapp.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ch.bbw.m183.vulnerapp.repository.UserRepository;
import ch.bbw.m183.vulnerapp.service.RestfulFormService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository
                .findById(username)
                .map(userEntity -> new User(userEntity.getUsername(), userEntity.getPassword(), List.of(
                        new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().name())
                )))
                .orElseThrow(() -> new UsernameNotFoundException("User couldn't be found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RestfulFormService restfulFormService) {
        return http.formLogin(restfulFormService.restfulFormLogin())
                .exceptionHandling(restfulFormService.unauthorizedPerDefault())
                .csrf(x -> x.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin123/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/fakelogin").permitAll()
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/blog/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .build();
    }
}