package com.github.anbeld.ForoHub.infra.security;

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

    // Revisa las request, las válida y las realiza si la validación es correcta
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();

        // Omitir validación si pertenece a alguna de las siguientes condiciones
        if (omitirValidacion(requestURI, requestMethod)) {
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
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    // Omite la validación para ciertas rutas y métodos
    private boolean omitirValidacion(String requestURI, String requestMethod) {
        if (requestMethod.equals("POST")) {
            return requestURI.equals("/login") || requestURI.equals("/estudiantes") || requestURI.equals("/docentes");
        } else {
            return requestURI.startsWith("/swagger-ui.html") ||
                    requestURI.startsWith("/v3/api-docs") ||
                    requestURI.startsWith("/swagger-ui") ||
                    requestURI.startsWith("/swagger-ui/index.html#/");
        }
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
