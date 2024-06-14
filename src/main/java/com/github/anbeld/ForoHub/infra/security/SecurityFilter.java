package com.github.anbeld.ForoHub.infra.security;

import com.github.anbeld.ForoHub.infra.errores.ValidacionDeIntegridad;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    private Claims claims;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        // Omitir validación si pertenece a alguna de las siguientes condiciones
        if (requestURI.equals("/login") && requestMethod.equals("POST") ||
                requestURI.equals("/estudiantes") && requestMethod.equals("POST") ||
                requestURI.equals("/docentes") && requestMethod.equals("POST") ||
                requestURI.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtener el token del header
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.substring(7);
            var nombreUsuario = jwtService.extractUserName(token); // Extraer nombre de usuario
            claims = jwtService.extractAllClaims(token);
            if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Token válido
                var usuario = authenticationService.loadUserByUsername(nombreUsuario);
                if (jwtService.validateToken(token, usuario)) {  // Asegurarse de que el token es válido
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new ValidacionDeIntegridad("Ingresar token de autorización válido");
                }
            } else {
                throw new ValidacionDeIntegridad("El token no es válido");
            }
        } else {
            throw new ValidacionDeIntegridad("Debe ingresar un token");
        }
        filterChain.doFilter(request, response);
    }

    public Boolean isAdmin() {
        return claims != null && "ADMIN".equalsIgnoreCase((String) claims.get("userRole"));
    }

    public Boolean isEstudiante() {
        return claims != null && "ESTUDIANTE".equalsIgnoreCase((String) claims.get("userRole"));
    }

    public Boolean isDocente() {
        return claims != null && "DOCENTE".equalsIgnoreCase((String) claims.get("userRole"));
    }
}
