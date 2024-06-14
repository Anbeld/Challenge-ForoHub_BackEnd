package com.github.anbeld.ForoHub.infra.security;

import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${api.security.token.secret}")
    private String SECRET_API;

    private SecretKey secretKey;

    public String extractUserName(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String userName, Perfil userRole){
        Map<String, Object> claims = new HashMap<>();
        claims.put("userRole", userRole);
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 100*60*60*10))
                .signWith(getSignInKey(), Jwts.SIG.HS256).compact();
    }

    @PostConstruct
    public void init() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256); // 256 bits para HS256
            secretKey = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar la clave: " + e.getMessage());
        }
    }

    private SecretKey getSignInKey() {
        return secretKey;
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        System.out.println("Obtenido" + userName);
        if (isTokenExpired(token)){
            System.out.println("Por aquí no fue");
        }
        System.out.println("Obtenido de la base" + userDetails.getUsername());
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
