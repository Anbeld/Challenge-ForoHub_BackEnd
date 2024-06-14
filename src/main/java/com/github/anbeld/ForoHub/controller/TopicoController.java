package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.topico.DatosInputDeleteTopico;
import com.github.anbeld.ForoHub.domain.topico.DatosInputTopico;
import com.github.anbeld.ForoHub.domain.topico.DatosOutputTopico;
import com.github.anbeld.ForoHub.domain.topico.TopicoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/topicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topicos", description = "Operaciones relacionadas con los tópicos")
public class TopicoController {

    @Autowired
    private TopicoService service;

    // Registra un tópico
    @PostMapping
    @Tag(name = "Registrar Tópico", description = "Registra un nuevo tópico en la base de datos")
    public ResponseEntity<DatosOutputTopico> registrarTopico(@RequestBody @Valid DatosInputTopico datos){
        var response = service.registrarTopico(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos activos
    @GetMapping
    @Tag(name = "Obtener Tópicos Activos", description = "Obtiene un listado de tópicos activos")
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos registrados por status
    @GetMapping(path = "/{status}")
    @Tag(name = "Obtener Tópicos por Estado", description = "Obtiene un listado de tópicos por su estado (activo o inactivo)")
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosPorStatus(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable boolean status){
        var response = service.obtenerTopicosPorStatus(paginacion, status);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos
    @GetMapping(path = "/all")
    @Tag(name = "Obtener Todos los Tópicos", description = "Obtiene un listado de todos los tópicos registrados")
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Delete lógico de un tópico
    @DeleteMapping
    @Tag(name = "Cerrar Tópico", description = "Cierra un tópico de forma lógica en la base de datos")
    public ResponseEntity cerrarTopico(@RequestBody @Valid DatosInputDeleteTopico datos){
        service.cerrarTopico(datos);
        return ResponseEntity.noContent().build();
    }
}
