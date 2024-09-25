package com.microservice.userssecurity.securityConfig.Jwt;

import com.microservice.userssecurity.model.UserEntity;
import com.microservice.userssecurity.repository.IRoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.microservice.userssecurity.model.ERole;
import com.microservice.userssecurity.model.RoleEntity;

@Component
@Slf4j
public class JwtUtils {
    @Autowired
    private IRoleRepository roleRepository;
    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.time.expiration}")
    private String timeExpiration;

    //creando token con sus claims y su firma, no así el header.
    public String generateAccessToken(UserEntity userEntity) {
        Set<String> roles = userEntity.getRoles().stream()
                .map(RoleEntity::getName)
                .map(ERole::name)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(userEntity.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Obtener firma del token:
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Validar el token de acceso:
    public boolean isTokenValid(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch(Exception e){
            log.error("Invalid token, error: " .concat(e.getMessage()));
            return false;
        }
    }

    //Obtener todos los claims del token:
    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //obtener un solo claim del token:
    public <T> T getClaim(String token, Function<Claims, T> claimsFunction){
        Claims claims = extractAllClaims(token);
        return claimsFunction.apply(claims);
    }

    //obtener el email del token
    public String getEmailFromToken(String token){
        return getClaim(token,Claims::getSubject);
    }

    //Obtener roles del token
    public Set<RoleEntity> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSignatureKey())
                .parseClaimsJws(token)
                .getBody();

        List<String> roleNames = claims.get("roles", List.class);

        Set<RoleEntity> roles = new HashSet<>();
        for (String roleName : roleNames) {
            RoleEntity role = roleRepository.findByName(ERole.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(role);
        }
        return roles;
    }
}
