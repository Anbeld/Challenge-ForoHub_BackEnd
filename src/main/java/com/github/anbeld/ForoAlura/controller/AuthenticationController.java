package com.github.anbeld.ForoAlura.controller;

import com.github.anbeld.ForoAlura.domain.usuario.DatosInputLoginUsuario;
import com.github.anbeld.ForoAlura.domain.usuario.DatosOutputLoginUsuario;
import com.github.anbeld.ForoAlura.domain.usuario.DatosOutputUsuario;
import com.github.anbeld.ForoAlura.domain.usuario.UsuarioService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private UsuarioService service;

    // Verifica la información para dar acceso al usuario
    @PostMapping
    @Transactional
    public ResponseEntity<DatosOutputLoginUsuario> login(@RequestBody @Valid DatosInputLoginUsuario datos) {
        var response = service.login(datos);
        return ResponseEntity.ok(response);
    }
}
