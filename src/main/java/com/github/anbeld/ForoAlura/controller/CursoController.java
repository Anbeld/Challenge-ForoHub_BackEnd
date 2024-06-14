package com.github.anbeld.ForoAlura.controller;

import com.github.anbeld.ForoAlura.domain.curso.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    @Autowired
    private CursoService service;

    // Registra un curso
    @PostMapping
    @Transactional
    public ResponseEntity<DatosOutputCurso> registrarCurso(@RequestBody @Valid DatosInputRegistrarCurso datos){
        var response = service.registrarCurso(datos);
        return ResponseEntity.ok(response);
    }

    // Registra un estudiante a un curso
    @PostMapping("/registrar")
    public ResponseEntity<DatosOutputRegistrarEstudianteCurso> registrarEstudianteEnCurso(@RequestBody @Valid DatosInputRegistrarEstudianteCurso datos){
        var response = service.registrarEstudiante(datos);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursos(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion){
        var response = service.obtenerListadoCursos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Muestra todos los cursos asociados a un usuario
    // Si es docente, muestra todos los cursos que ha creado
    // Si es estudiante, muestra todos los cursos en lo que se encuentra registrado
    @GetMapping("/{id}")
    public ResponseEntity<Page<DatosOutputCurso>> obtenerListadoCursosPorIdAutor(@PageableDefault(page = 0, size = 10, sort = {"nombre"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerListadoCursosPorIdAutor(paginacion, id);
        return ResponseEntity.ok(response);
    }

}
