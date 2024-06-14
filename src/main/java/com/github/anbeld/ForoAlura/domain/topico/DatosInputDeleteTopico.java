package com.github.anbeld.ForoAlura.domain.topico;

import jakarta.validation.constraints.NotNull;

public record DatosInputDeleteTopico(
        @NotNull
        Long topico_id,
        @NotNull
        Long autor_id
) {
}
