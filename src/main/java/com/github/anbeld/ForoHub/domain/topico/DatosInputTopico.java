package com.github.anbeld.ForoHub.domain.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosInputTopico(
        @NotNull
        Long curso_id,
        @NotNull
        Long usuario_id,
        @NotBlank
        String titulo,
        @NotBlank
        String mensaje
) {
}
