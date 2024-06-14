package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.respuesta.DatosInputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.DatosOutputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.RespuestaService;
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
@RequestMapping(path = "/respuestas")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Respuestas", description = "Operaciones relacionadas con las respuestas a los tópicos")
public class RespuestaController {

    @Autowired
    private RespuestaService service;

    // Registra una respuesta
    @PostMapping
    @Tag(name = "Registrar Respuesta", description = "Registra una nueva respuesta en la base de datos")
    public ResponseEntity<DatosOutputRespuesta> registrarRespuesta(@RequestBody @Valid DatosInputRespuesta datos){
        var response = service.registrarRespuesta(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas con tópicos activos
    @GetMapping
    @Tag(name = "Obtener Respuestas Activas", description = "Obtiene un listado de respuestas con tópicos activos")
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerRespuestasPorTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por autor_id
    @GetMapping(path = "/autor/{id}")
    @Tag(name = "Obtener Respuestas por Autor", description = "Obtiene un listado de respuestas por el ID del autor")
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorAutorId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorAutorId(paginacion, id);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por topico_id
    @GetMapping(path = "/topico/{id}")
    @Tag(name = "Obtener Respuestas por Tópico", description = "Obtiene un listado de respuestas por el ID del tópico")
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicoId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorTopicoId(paginacion, id);
        return ResponseEntity.ok(response);
    }
}
