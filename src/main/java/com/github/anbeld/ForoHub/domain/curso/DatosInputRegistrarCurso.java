package com.github.anbeld.ForoHub.domain.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosInputRegistrarCurso(
        @NotBlank
        String nombre,
        @NotNull
        Categoria categoria,
        @NotNull
        Long docenteId
) {
}
