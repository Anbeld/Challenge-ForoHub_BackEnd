package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "/docentes")
@Tag(name = "Docentes", description = "Operaciones relacionadas con los docentes")
public class DocenteController {

    @Autowired
    private UsuarioService service;

    // Registra un docente
    @SneakyThrows
    @PostMapping()
    @Transactional
    @Operation(
            summary = "Registrar Docente",
            description = "Registra un nuevo docente",
            tags = {"Docentes", "POST"})
    public ResponseEntity<DatosOutputUsuario> registrarDocente(@RequestBody @Valid DatosInputRegistrarUsuario datos, UriComponentsBuilder uriComponentsBuilder) {
        var response = service.registrarUsuario(datos, Perfil.DOCENTE, uriComponentsBuilder);
        URI url = new URI(URLDecoder.decode(response.getUrl(), StandardCharsets.UTF_8));
        return ResponseEntity.created(url).body(new DatosOutputUsuario(response));
    }

    // Obtiene el listado de docentes
    @GetMapping()
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Listado de Docentes",
            description = "Obtiene el listado de todos los docentes registrados",
            tags = {"Docentes", "GET"})
    public ResponseEntity<Page<DatosOutputUsuario>> listadoDocentesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion) {
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el docente registrado con ese ID
    @GetMapping(path = "/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Obtener Docente por ID",
            description = "Obtiene un docente registrado por su ID",
            tags = {"Docentes", "GET"})
    public ResponseEntity<DatosOutputDocentePorId> obtenerDocentePorId(@PathVariable Long id) {
        var response = service.obtenerDocentePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del docente
    @PutMapping()
    @SecurityRequirement(name = "bearer-key")
    @Transactional
    @Operation(
            summary = "Actualizar Contraseña de Docente",
            description = "Actualiza la contraseña de un docente",
            tags = {"Docentes", "PUT"})
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordDocente(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos) {
        var response = service.actualizarPasswordUsuario(datos, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un docente
    @DeleteMapping(path = "/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Transactional
    @Operation(
            summary = "Eliminar Docente",
            description = "Elimina lógicamente un docente de la base de datos",
            tags = {"Docentes", "DELETE"})
    public ResponseEntity deleteLogicoDocente(@PathVariable Long id) {
        service.desactivarUsuario(id, Perfil.DOCENTE);
        return ResponseEntity.noContent().build();
    }
}
