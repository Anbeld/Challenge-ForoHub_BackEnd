package com.github.anbeld.ForoHub.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosInputActualizarPasswordUsuario(
        @NotNull
        Long id,
        @NotBlank
        String email,
        @JsonAlias("actual_contraseña")
        @NotBlank
        String current_password,
        @JsonAlias("nueva_contraseña")
        @NotBlank
        String new_password
) {
}
