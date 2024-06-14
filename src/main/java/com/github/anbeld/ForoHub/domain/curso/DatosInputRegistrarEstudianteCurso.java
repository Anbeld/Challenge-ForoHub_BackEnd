package com.github.anbeld.ForoHub.domain.curso;

import jakarta.validation.constraints.NotNull;

public record DatosInputRegistrarEstudianteCurso(
        @NotNull
        Long curso_id,
        @NotNull
        Long estudiante_id
) {
}
