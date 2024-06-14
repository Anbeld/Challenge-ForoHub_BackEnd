package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.curso.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/cursos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Cursos", description = "Operaciones relacionadas con los cursos")
public class CursoController {
    @Autowired
    private CursoService service;

    // Registra un curso
    @PostMapping
    @Transactional
    @Tag(name = "Registrar Curso", description = "Registra un nuevo curso en la base de datos")
    public ResponseEntity<DatosOutputCurso> registrarCurso(@RequestBody @Valid DatosInputRegistrarCurso datos){
        var response = service.registrarCurso(datos);
        return ResponseEntity.ok(response);
    }

    // Registra un estudiante a un curso
    @PostMapping(path = "/registrar")
    @Tag(name = "Registrar Estudiante en Curso", description = "Registra un estudiante en un curso existente")
    public ResponseEntity<DatosOutputRegistrarEstudianteCurso> registrarEstudianteEnCurso(@RequestBody @Valid DatosInputRegistrarEstudianteCurso datos){
        var response = service.registrarEstudiante(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de cursos
    @GetMapping
    @Tag(name = "Obtener Listado de Cursos", description = "Obtiene el listado de todos los cursos registrados")
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursos(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion){
        var response = service.obtenerListadoCursos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de cursos registrados para el usuario
    // Si es docente, muestra todos los cursos que ha creado
    // Si es estudiante, muestra todos los cursos en lo que se encuentra registrado
    @GetMapping(path = "/{id}")
    @Tag(name = "Obtener Listado de Cursos por Autor", description = "Obtiene el listado de cursos registrados por un autor específico")
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursosPorIdAutor(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerListadoCursosPorIdAutor(paginacion, id);
        return ResponseEntity.ok(response);
    }

}
