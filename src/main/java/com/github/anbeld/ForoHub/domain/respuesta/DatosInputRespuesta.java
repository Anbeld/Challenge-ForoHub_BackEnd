package com.github.anbeld.ForoHub.domain.respuesta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosInputRespuesta(
        @NotNull
        Long topico_id,
        @NotNull
        Long autor_id,
        @NotBlank
        String respuesta
) {
}
