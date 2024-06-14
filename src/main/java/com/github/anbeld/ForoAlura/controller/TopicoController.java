package com.github.anbeld.ForoAlura.controller;

import com.github.anbeld.ForoAlura.domain.topico.DatosInputDeleteTopico;
import com.github.anbeld.ForoAlura.domain.topico.DatosInputTopico;
import com.github.anbeld.ForoAlura.domain.topico.DatosOutputTopico;
import com.github.anbeld.ForoAlura.domain.topico.TopicoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoService service;

    // Registra un tópico
    @PostMapping
    public ResponseEntity<DatosOutputTopico> registrarTopico(@RequestBody @Valid DatosInputTopico datos){
        var response = service.registrarTopico(datos);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos activos
    @GetMapping
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosActivos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicosActivos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos registrados por status
    @GetMapping("/{status}")
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicosPorStatus(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion, @PathVariable boolean status){
        var response = service.obtenerTopicosPorStatus(paginacion, status);
        return ResponseEntity.ok(response);
    }

    // Obtiene el listado de tópicos
    @GetMapping("/all")
    public ResponseEntity<Page<DatosOutputTopico>> obtenerTopicos(@PageableDefault(page = 0, size = 10, sort = {"fechaCreacion"}) Pageable paginacion){
        var response = service.obtenerTopicos(paginacion);
        return ResponseEntity.ok(response);
    }

    // Delete lógico de un tópico
    @DeleteMapping
    public ResponseEntity cerrarTopico(@RequestBody @Valid DatosInputDeleteTopico datos){
        service.cerrarTopico(datos);
        return ResponseEntity.noContent().build();
    }
}
