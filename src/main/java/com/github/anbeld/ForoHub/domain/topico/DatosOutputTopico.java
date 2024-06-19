package com.github.anbeld.ForoHub.domain.topico;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDateTime;

public record DatosOutputTopico(
        @JsonAlias("id")
        Long topico_id,
        String autor,
        String titulo,
        String mensaje,
        LocalDateTime fecha_creacion,
        Boolean resuelto,
        String nombre_curso
) {
    public DatosOutputTopico (Topico topico){
        this(topico.getId(), topico.getAutor().getUserName(), topico.getTitulo(), topico.getMensaje(), topico.getFechaCreacion(), topico.isResuelto(), topico.getCurso().getNombre());
    }
}
