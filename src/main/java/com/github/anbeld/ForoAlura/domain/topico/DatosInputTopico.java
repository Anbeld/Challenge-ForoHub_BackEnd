package com.github.anbeld.ForoAlura.domain.topico;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

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
