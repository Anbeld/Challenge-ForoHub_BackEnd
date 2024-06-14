package com.github.anbeld.ForoHub.domain.respuesta;

import java.time.LocalDateTime;

public record DatosOutputRespuesta(
        Long respuesta_id,
        String tituloTopico,
        String curso,
        String autorRespuesta,
        LocalDateTime fechaCreacion,
        String respuesta
) {
    public DatosOutputRespuesta(Respuesta respuesta) {
        this(respuesta.getId(),
                respuesta.getTopico().getTitulo(),
                respuesta.getTopico().getCurso().getNombre(),
                respuesta.getAutor().getUserName(),
                respuesta.getFechaCreacion(),
                respuesta.getRespuesta());
    }
}
