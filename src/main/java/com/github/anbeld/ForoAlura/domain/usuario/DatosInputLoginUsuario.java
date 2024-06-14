package com.github.anbeld.ForoAlura.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DatosInputLoginUsuario(
        @NotBlank
        String email,
        @NotBlank
        String password
) {
}
