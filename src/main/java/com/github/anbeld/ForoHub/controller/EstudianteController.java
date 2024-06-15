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
@RequestMapping(path = "/estudiantes")
@Tag(name = "Estudiantes", description = "Operaciones relacionadas con los estudiantes")
public class EstudianteController {

    @Autowired
    private UsuarioService service;

    // Registra un estudiante
    @SneakyThrows
    @PostMapping()
    @Transactional
    @Operation(
            summary = "Registrar Estudiante",
            description = "Registra un nuevo estudiante en la base de datos",
            tags = {"Estudiantes", "POST"})
    public ResponseEntity<DatosOutputUsuario> registrarEstudiante(@RequestBody @Valid DatosInputRegistrarUsuario datos, UriComponentsBuilder uriComponentsBuilder) {
        var response = service.registrarUsuario(datos, Perfil.ESTUDIANTE, uriComponentsBuilder);
        URI url = new URI(URLDecoder.decode(response.getUrl(), StandardCharsets.UTF_8));
        return ResponseEntity.created(url).body(new DatosOutputUsuario(response));
    }

    // Obtiene el listado de estudiantes
    @GetMapping()
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Listado de Estudiantes",
            description = "Obtiene el listado de todos los estudiantes registrados",
            tags = {"Estudiantes", "GET"})
    public ResponseEntity<Page<DatosOutputUsuario>> listadoEstudiantesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion) {
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el estudiante registrado con ese ID
    @GetMapping(path = "/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Operation(
            summary = "Obtener Estudiante por ID",
            description = "Obtiene un estudiante registrado por su ID",
            tags = {"Estudiantes", "GET"})
    public ResponseEntity<DatosOutputEstudiantePorId> obtenerEstudiantePorId(@PathVariable Long id) {
        var response = service.obtenerEstudiantePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del estudiante
    @PutMapping()
    @SecurityRequirement(name = "bearer-key")
    @Transactional
    @Operation(
            summary = "Actualizar Contraseña de Estudiante",
            description = "Actualiza la contraseña de un estudiante",
            tags = {"Estudiantes", "PUT"})
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordEstudiante(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos) {
        var response = service.actualizarPasswordUsuario(datos, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un estudiante
    @DeleteMapping(path = "/{id}")
    @SecurityRequirement(name = "bearer-key")
    @Transactional
    @Operation(
            summary = "Eliminar Estudiante",
            description = "Elimina lógicamente un estudiante del sistema",
            tags = {"Estudiantes", "DELETE"})
    public ResponseEntity deleteLogicoEstudiante(@PathVariable Long id) {
        service.desactivarUsuario(id, Perfil.ESTUDIANTE);
        return ResponseEntity.noContent().build();
    }
}
