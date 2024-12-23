package com.dss.practica1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class Config {

	@Bean
    public Map<String, String> tokenStore() {
        return new HashMap<>();
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenAuthenticationFilter tokenFilter) throws Exception {
		http
        .csrf()
            .ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")) // Desactivar CSRF solo para rutas API
            .and()
        .authorizeHttpRequests()
            .requestMatchers(new AntPathRequestMatcher("/login")).permitAll() // Permitir acceso público a /login
            .requestMatchers(new AntPathRequestMatcher("/logout")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll() // Permitir acceso a H2 Console
            .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll() // Permitir API auth pública
            .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated() // Rutas protegidas con token
            .anyRequest().authenticated() // Otras rutas protegidas con sesión
            .and()
        .formLogin()
            .loginPage("/login") // Página personalizada de inicio de sesión
            .defaultSuccessUrl("/products", true)
            .permitAll()
            .and()
        .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .permitAll()
            .and()
        .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

		
	    return http.build();
	}

    @Bean
    public PasswordEncoder passEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
