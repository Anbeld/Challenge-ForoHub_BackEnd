package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.*;
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
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con los usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // Registra un estudiante
    @PostMapping(path = "/estudiantes")
    @Transactional
    @Tag(name = "Registrar Estudiante", description = "Registra un nuevo estudiante en la base de datos")
    public ResponseEntity<DatosOutputUsuario> registrarEstudiante(@RequestBody @Valid DatosInputRegistrarUsuario datos){
        var response = service.registrarUsuario(datos, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de estudiantes
    @GetMapping(path = "/estudiantes")
    @Tag(name = "Listado de Estudiantes", description = "Obtiene el listado de todos los estudiantes registrados")
    public ResponseEntity<Page<DatosOutputUsuario>> listadoEstudiantesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion){
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el estudiante registrado con ese ID
    @GetMapping(path = "/estudiantes/{id}")
    @Tag(name = "Obtener Estudiante por ID", description = "Obtiene un estudiante registrado por su ID")
    public ResponseEntity<DatosOutputEstudiantePorId> obtenerEstudiantePorId(@PathVariable Long id){
        var response = service.obtenerEstudiantePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del estudiante
    @PutMapping(path = "/estudiantes")
    @Transactional
    @Tag(name = "Actualizar Contraseña de Estudiante", description = "Actualiza la contraseña de un estudiante")
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordEstudiante(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos){
        var response = service.actualizarPasswordUsuario(datos, Perfil.ESTUDIANTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un estudiante
    @DeleteMapping(path = "/estudiantes/{id}")
    @Transactional
    @Tag(name = "Eliminar Estudiante", description = "Elimina lógicamente un estudiante del sistema")
    public ResponseEntity deleteLogicoEstudiante(@PathVariable Long id){
        service.desactivarUsuario(id, Perfil.ESTUDIANTE);
        return ResponseEntity.noContent().build();
    }

    // Registra un docente
    @PostMapping(path = "/docentes")
    @Transactional
    @Tag(name = "Registrar Docente", description = "Registra un nuevo docente")
    public ResponseEntity<DatosOutputUsuario> registrarDocente(@RequestBody @Valid DatosInputRegistrarUsuario datos){
        var response = service.registrarUsuario(datos, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de docentes
    @GetMapping(path = "/docentes")
    @Tag(name = "Listado de Docentes", description = "Obtiene el listado de todos los docentes registrados")
    public ResponseEntity<Page<DatosOutputUsuario>> listadoDocentesRegistrados(@PageableDefault(page = 0, size = 10, sort = {"userName"}) Pageable paginacion){
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Obtiene el docente registrado con ese ID
    @GetMapping(path = "/docentes/{id}")
    @Tag(name = "Obtener Docente por ID", description = "Obtiene un docente registrado por su ID")
    public ResponseEntity<DatosOutputDocentePorId> obtenerDocentePorId(@PathVariable Long id){
        var response = service.obtenerDocentePorId(id);
        return ResponseEntity.ok(response);
    }

    // Actualiza la contraseña del docente
    @PutMapping(path = "/docentes")
    @Tag(name = "Actualizar Contraseña de Docente", description = "Actualiza la contraseña de un docente")
    @Transactional
    public ResponseEntity<DatosOutputUsuario> actualizarPasswordDocente(@RequestBody @Valid DatosInputActualizarPasswordUsuario datos){
        var response = service.actualizarPasswordUsuario(datos, Perfil.DOCENTE);
        return ResponseEntity.ok(response);
    }

    // Delete lógido de un docente
    @DeleteMapping(path = "/docentes/{id}")
    @Transactional
    @Tag(name = "Eliminar Docente", description = "Elimina lógicamente un docente de la base de datos")
    public ResponseEntity deleteLogicoDocente(@PathVariable Long id){
        service.desactivarUsuario(id, Perfil.DOCENTE);
        return ResponseEntity.noContent().build();
    }
}
