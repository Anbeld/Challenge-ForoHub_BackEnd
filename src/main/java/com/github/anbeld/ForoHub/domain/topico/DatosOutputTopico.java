package com.github.anbeld.ForoHub.domain.topico;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDateTime;

public record DatosOutputTopico(
        @JsonAlias("id")
        Long topico_id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        Boolean resuelto,
        String nombreCurso
) {
    public DatosOutputTopico (Topico topico){
        this(topico.getId(), topico.getTitulo(), topico.getMensaje(), topico.getFechaCreacion(), topico.isResuelto(), topico.getCurso().getNombre());
    }
}
