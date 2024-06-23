package com.github.anbeld.ForoHub.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record DatosInputLoginUsuario(
        @NotBlank
        String email,
        @JsonAlias("contraseña")
        @NotBlank
        String password
) {
}
