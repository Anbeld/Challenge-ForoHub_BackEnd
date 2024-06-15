package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.respuesta.DatosInputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.DatosOutputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.RespuestaService;
import com.github.anbeld.ForoHub.domain.topico.DatosOutputTopico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping(path = "/respuestas")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Respuestas", description = "Operaciones relacionadas con las respuestas a los tópicos")
public class RespuestaController {

    @Autowired
    private RespuestaService service;

    // Registra una respuesta
    @SneakyThrows
    @PostMapping
    @Operation(
            summary = "Registrar Respuesta",
            description = "Registra una nueva respuesta en la base de datos",
            tags = { "Respuestas", "POST" })
    public ResponseEntity<DatosOutputRespuesta> registrarRespuesta(@RequestBody @Valid DatosInputRespuesta datos, UriComponentsBuilder uriComponentsBuilder){
        var response = service.registrarRespuesta(datos, uriComponentsBuilder);
        URI url = new URI(URLDecoder.decode(response.getUrl(), StandardCharsets.UTF_8));
        return ResponseEntity.created(url).body(new DatosOutputRespuesta(response));
    }

    // Obtiene un listado de respuestas con tópicos activos
    @GetMapping
    @Operation(
            summary = "Obtener Respuestas Activas",
            description = "Obtiene un listado de respuestas con tópicos activos",
            tags = { "Respuestas", "GET" })
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerRespuestasPorTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por autor_id
    @GetMapping(path = "/autor/{id}")
    @Operation(
            summary = "Obtener Respuestas por Autor",
            description = "Obtiene un listado de respuestas por el ID del autor",
            tags = { "Respuestas", "GET" })
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorAutorId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorAutorId(paginacion, id);
        return ResponseEntity.ok(response);
    }

    // Obtiene un listado de respuestas por topico_id
    @GetMapping(path = "/topico/{id}")
    @Operation(
            summary = "Obtener Respuestas por Tópico",
            description = "Obtiene un listado de respuestas por el ID del tópico",
            tags = { "Respuestas", "GET" })
    public ResponseEntity<Page<DatosOutputRespuesta>> obtenerRespuestasPorTopicoId(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable Long id){
        var response = service.obtenerRespuestasPorTopicoId(paginacion, id);
        return ResponseEntity.ok(response);
    }
}
