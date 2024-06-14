package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Registra un estudiante
    @PostMapping("/estudiantes")
    @Transactional
    public ResponseEntity<DatosOutputUsuario> registrarEstudiante(@RequestBody @Valid DatosInputRegistrarUsuario datos){
        var response = service.registrarUsuario(datos, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de estudiantes
    @GetMapping("/estudiantes")
    public ResponseEntity<Page<DatosOutputUsuario>> listadoEstudiantesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion){
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el estudiante registrado con ese ID
    @GetMapping("/estudiantes/{id}")
    public ResponseEntity<DatosOutputEstudiantePorId> obtenerEstudiantePorId(@PathVariable Long id){
        var response = service.obtenerEstudiantePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del estudiante
    @PutMapping("/estudiantes")
    @Transactional
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordEstudiante(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos){
        var response = service.actualizarPasswordUsuario(datos, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un estudiante
    @DeleteMapping("/estudiantes/{id}")
    @Transactional
    public ResponseEntity deleteLogicoEstudiante(@PathVariable Long id){
        service.desactivarUsuario(id, Perfil.ESTUDIANTE);
        return ResponseEntity.noContent().build();
    }

    // Registra un docente
    @PostMapping("/docentes")
    @Transactional
    public ResponseEntity<DatosOutputUsuario> registrarDocente(@RequestBody @Valid DatosInputRegistrarUsuario datos){
        var response = service.registrarUsuario(datos, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de docentes
    @GetMapping("/docentes")
    public ResponseEntity<Page<DatosOutputUsuario>> listadoDocentesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion){
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el docente registrado con ese ID
    @GetMapping("/docentes/{id}")
    public ResponseEntity<DatosOutputDocentePorId> obtenerDocentePorId(@PathVariable Long id){
        var response = service.obtenerDocentePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del docente
    @PutMapping("/docentes")
    @Transactional
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordDocente(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos){
        var response = service.actualizarPasswordUsuario(datos, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un docente
    @DeleteMapping("/docentes/{id}")
    @Transactional
    public ResponseEntity deleteLogicoDocente(@PathVariable Long id){
        service.desactivarUsuario(id, Perfil.DOCENTE);
        return ResponseEntity.noContent().build();
    }
}
