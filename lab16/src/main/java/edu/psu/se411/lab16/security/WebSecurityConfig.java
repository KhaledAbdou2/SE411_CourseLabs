package edu.psu.se411.lab16.security;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableWebSecurity
public class WebSecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(requests -> requests
   .requestMatchers("/").permitAll()
   .requestMatchers("/dashboard/**").hasRole("USER")
   .requestMatchers("/admindashboard/**").hasRole("ADMIN")
   .anyRequest().authenticated())
   .formLogin(form -> form.permitAll())
   .logout(logout -> logout.permitAll());
  return http.build();
 }
 @Bean UserDetailsService userDetailsService(PasswordEncoder encoder) {
  return new InMemoryUserDetailsManager(
   User.withUsername("usera").password(encoder.encode("pwd123")).roles("USER").build(),
   User.withUsername("admin").password(encoder.encode("admin123")).roles("ADMIN").build());
 }
 @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
