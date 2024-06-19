package com.github.anbeld.ForoHub.controller;
import com.github.anbeld.ForoHub.domain.topico.DatosInputTopico;
import com.github.anbeld.ForoHub.domain.topico.DatosOutputTopico;
import com.github.anbeld.ForoHub.domain.topico.TopicoService;
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
@RequestMapping(path = "/api/topicos")
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Topicos", description = "Operaciones relacionadas con los tópicos")
public class TopicoController {

    @Autowired
    private TopicoService service;

    // Registra un tópico
    @SneakyThrows
    @PostMapping
    @Operation(
            summary = "Registrar Tópico",
            description = "Registra un nuevo tópico en la base de datos",
            tags = { "Topicos", "POST" })
    public ResponseEntity<DatosOutputTopico> registrarTopico(@RequestBody @Valid DatosInputTopico datos, UriComponentsBuilder uriComponentsBuilder){
        var response = service.registrarTopico(datos, uriComponentsBuilder);
        URI url = new URI(URLDecoder.decode(response.getUrl(), StandardCharsets.UTF_8));
        return ResponseEntity.created(url).body(new DatosOutputTopico(response));
    }

    // Obtiene el listado de tópicos activos
    @GetMapping
    @Operation(
            summary = "Obtener Tópicos Activos",
            description = "Obtiene un listado de tópicos activos",
            tags = { "Topicos", "GET" })
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos registrados por status
    @GetMapping(path = "/{status}")
    @Operation(
            summary = "Obtener Tópicos por Estado",
            description = "Obtiene un listado de tópicos por su estado (activo o inactivo)",
            tags = { "Topicos", "GET" })
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosPorStatus(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable boolean status){
        var response = service.obtenerTopicosPorStatus(paginacion, status);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos
    @GetMapping(path = "/all")
    @Operation(
            summary = "Obtener Todos los Tópicos",
            description = "Obtiene un listado de todos los tópicos registrados",
            tags = { "Topicos", "GET" })
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Delete lógico de un tópico
    @DeleteMapping(path = "/{id}")
    @Operation(
            summary = "Cerrar Tópico",
            description = "Cierra un tópico de forma lógica en la base de datos",
            tags = { "Topicos", "DELETE" })
    public ResponseEntity cerrarTopico(@PathVariable Long id){
        service.cerrarTopico(id);
        return ResponseEntity.noContent().build();
    }
}
