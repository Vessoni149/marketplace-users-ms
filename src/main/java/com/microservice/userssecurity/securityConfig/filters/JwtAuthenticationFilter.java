package com.microservice.userssecurity.securityConfig.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.userssecurity.dto.LoginDto;
import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.IUserRepository;
import com.microservice.userssecurity.securityConfig.Jwt.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    private JwtUtils jwtUtils;
    private IUserRepository userRepo;
    public JwtAuthenticationFilter(JwtUtils jwtUtils, IUserRepository userRepo){
        this.jwtUtils = jwtUtils;
        this.userRepo = userRepo;
    }

    //sirve para intentar autneticarse
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginDto loginDto;
        String email = "";
        String password = "";
        try {
            loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);
            email = loginDto.getEmail();
            password = loginDto.getPassword();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password
        );
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        String email = user.getUsername();
        Optional<UserEntity> userEntityOptional = userRepo.findByEmail(email);

        if (userEntityOptional.isPresent()) {
            UserEntity userEntity = userEntityOptional.get();
            String token = jwtUtils.generateAccessToken(userEntity);

            response.addHeader("Authorization", token);

            Map<String, Object> httpResponse = new HashMap<>();
            httpResponse.put("token", token);
            httpResponse.put("message", "Successful authentication.");
            httpResponse.put("Username", user.getUsername());
            httpResponse.put("fullName", userEntity.getFullName());
            httpResponse.put("user_id" , userEntity.getId());

            response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().flush();
        } else{
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("User not found with email: " + email);
            response.getWriter().flush();
        }

        super.successfulAuthentication(request,response,chain,authResult);
    }
}

