package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.curso.*;
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
@RequestMapping(path = "/api/cursos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cursos", description = "Operaciones relacionadas con los cursos")
public class CursoController {
    @Autowired
    private CursoService service;

    // Registra un curso
    @SneakyThrows
    @PostMapping
    @Transactional
    @Operation(
            summary = "Registrar Curso",
            description = "Registra un nuevo curso en la base de datos",
            tags = { "Cursos", "POST" })
    public ResponseEntity<DatosOutputCurso> registrarCurso(@RequestBody @Valid DatosInputRegistrarCurso datos, UriComponentsBuilder uriComponentsBuilder){
        var response = service.registrarCurso(datos, uriComponentsBuilder);
        URI url = new URI(URLDecoder.decode(response.getUrl(), StandardCharsets.UTF_8));
        return ResponseEntity.created(url).body(new DatosOutputCurso(response));
    }

    // Registra un estudiante a un curso
    @PostMapping(path = "/registrar")
    @Operation(
            summary = "Registrar Estudiante en Curso",
            description = "Registra un estudiante en un curso existente",
            tags = { "Cursos", "POST" })
    public ResponseEntity<DatosOutputRegistrarEstudianteCurso> registrarEstudianteEnCurso(@RequestBody @Valid DatosInputRegistrarEstudianteCurso datos){
        var response = service.registrarEstudiante(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de cursos
    @GetMapping
    @Operation(
            summary = "Obtener Listado de Cursos",
            description = "Obtiene el listado de todos los cursos registrados",
            tags = { "Cursos", "GET" })
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursos(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion){
        var response = service.obtenerListadoCursos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de cursos registrados para el usuario
    // Si es docente, muestra todos los cursos que ha creado
    // Si es estudiante, muestra todos los cursos en lo que se encuentra registrado
    @GetMapping(path = "/{id}")
    @Operation(
            summary = "Obtener Listado de Cursos por usuario",
            description = "Obtiene el listado de cursos registrados por un usuario específico",
            tags = { "Cursos", "GET" })
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursosPorIdUsuario(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerListadoCursosPorIdUsuario(paginacion, id);
        return ResponseEntity.ok(response);
    }

}
