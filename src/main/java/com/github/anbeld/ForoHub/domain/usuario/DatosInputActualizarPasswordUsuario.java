package com.github.anbeld.ForoHub.domain.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosInputActualizarPasswordUsuario(
        @NotNull
        Long id,
        @NotBlank
        String email,
        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword
) {
}
