package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.DatosInputLoginUsuario;
import com.github.anbeld.ForoHub.domain.usuario.DatosOutputLoginUsuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioService;
import com.github.anbeld.ForoHub.infra.security.DatosJWTToken;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/login")
@Tag(name = "Autenticacion", description = "Obtiene el token para el usuario ingresado que da acceso al resto de endpoint")
public class AuthenticationController {

    @Autowired
    private UsuarioService service;

    // Verifica la información para iniciar sesión
    @PostMapping
    @Transactional
    @Tag(name = "Login", description = "Verifica la información del usuario y le suministra un token de acceso para el resto de endpoint")
    public ResponseEntity<DatosJWTToken> login(@RequestBody @Valid DatosInputLoginUsuario datos) {
        var response = service.login(datos);
        return ResponseEntity.ok(response);
    }
}
