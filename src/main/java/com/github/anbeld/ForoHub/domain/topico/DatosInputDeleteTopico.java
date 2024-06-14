package com.github.anbeld.ForoHub.domain.topico;

import jakarta.validation.constraints.NotNull;

public record DatosInputDeleteTopico(
        @NotNull
        Long topico_id,
        @NotNull
        Long autor_id
) {
}
