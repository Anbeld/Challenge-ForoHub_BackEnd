package com.github.anbeld.ForoHub.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosInputLoginUsuario(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
