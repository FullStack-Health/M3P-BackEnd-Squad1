package br.com.senai.medicalone.config.security;

import br.com.senai.medicalone.services.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final CustomAuthorizationManager customAuthorizationManager;

    @Autowired
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtExceptionFilter jwtExceptionFilter,
                          UserDetailsServiceImpl userDetailsServiceImpl, CustomAuthorizationManager customAuthorizationManager) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtExceptionFilter = jwtExceptionFilter;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.customAuthorizationManager = customAuthorizationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/usuarios/login").permitAll()
                        .requestMatchers("/api/usuarios").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/exames/{id}").hasAnyRole("ADMIN", "MEDICO", "PACIENTE")
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new AccessDeniedHandlerImpl();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}