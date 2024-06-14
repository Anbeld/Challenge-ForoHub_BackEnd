package com.github.anbeld.ForoAlura.controller;

import com.github.anbeld.ForoAlura.domain.respuesta.DatosInputRespuesta;
import com.github.anbeld.ForoAlura.domain.respuesta.DatosOutputRespuesta;
import com.github.anbeld.ForoAlura.domain.respuesta.RespuestaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    @Autowired
    private RespuestaService service;

    // Registra una respuesta
    @PostMapping
    public ResponseEntity<DatosOutputRespuesta> registrarRespuesta(@RequestBody @Valid DatosInputRespuesta datos){
        var response = service.registrarRespuesta(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas con tópicos activos
    @GetMapping
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerRespuestasPorTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por autor_id
    @GetMapping("/autor/{id}")
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorAutorId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorAutorId(paginacion, id);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por topico_id
    @GetMapping("/topico/{id}")
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicoId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorTopicoId(paginacion, id);
        return ResponseEntity.ok(response);
    }
}
