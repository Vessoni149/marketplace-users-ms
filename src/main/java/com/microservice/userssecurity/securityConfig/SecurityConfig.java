package com.microservice.userssecurity.securityConfig;

import com.microservice.userssecurity.repository.IUserRepository;
import com.microservice.userssecurity.securityConfig.Jwt.JwtUtils;
import com.microservice.userssecurity.securityConfig.filters.JwtAuthenticationFilter;
import com.microservice.userssecurity.securityConfig.filters.JwtAuthorizationFilter;
import com.microservice.userssecurity.service.IUserService;
import com.microservice.userssecurity.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    JwtAuthorizationFilter authoritationFilter;
    @Autowired
    IUserRepository userRepo;

    @Autowired
    private UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, userRepo);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");
        return httpSecurity
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST,"/createUserEntity").permitAll()
                        .requestMatchers("/v1/csrf").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/get/{id}").permitAll()
                        .requestMatchers("/accessAdmin").hasRole("ADMIN")
                        .requestMatchers("/addSellerRole").hasRole("ADMIN")
                        .requestMatchers("/products/get/{userId}").hasRole("USER_SELLER")
                        .requestMatchers("/products/create").hasAnyRole("USER_SELLER", "ADMIN")
                        .requestMatchers("/products/add/{userId}/{productCode}").hasAnyRole("USER_SELLER","USER_BUYER", "ADMIN")
                        .requestMatchers("/addresses/addAddress/{userId}").hasAnyRole("USER_SELLER","USER_BUYER", "ADMIN")
                        .requestMatchers("/create-payment-intent").hasAnyRole("USER_SELLER","USER_BUYER", "ADMIN")
                        .anyRequest().authenticated()
                )
                /*.csrf(csrf -> {
                    csrf.csrfTokenRepository(cookieCsrfTokenRepository());
                })*/
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(userAuthenticationEntryPoint)
                )
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(authoritationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> login.loginPage("/login").permitAll())
                .build();

    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setHeaderName("CSRF_TOKEN");// Nombre personalizado para el token CSRF
        return repository;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

}


