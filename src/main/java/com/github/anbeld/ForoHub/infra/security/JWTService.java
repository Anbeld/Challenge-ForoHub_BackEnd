package com.github.anbeld.ForoHub.infra.security;

import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${api.security.token.secret_api}")
    private String SECRET_API;

    @Value("${api.security.token.secret_key}")
    private String SECRET_KEY_STRING;

    private SecretKey SECRET_KEY;

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
                .expiration(new Date(System.currentTimeMillis() + 100 * 60 * 60 * 2)) // Se determina como fecha de expiración dos horas apartir de creado el token
                .signWith(getSignInKey(), Jwts.SIG.HS256).compact();
    }

    @PostConstruct
    public void convertStringToSecretKey() {
        byte[] claveBytes = Base64.getDecoder().decode(SECRET_KEY_STRING);
        SECRET_KEY = new SecretKeySpec(claveBytes, "HmacSHA256");
    }

    private SecretKey getSignInKey() {
        return SECRET_KEY;
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
