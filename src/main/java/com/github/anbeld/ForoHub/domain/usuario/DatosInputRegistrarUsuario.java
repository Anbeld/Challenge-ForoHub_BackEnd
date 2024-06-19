package com.github.anbeld.ForoHub.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record DatosInputRegistrarUsuario(
        @JsonAlias("nombre")
        @NotBlank
        String user_name,
        @NotBlank
        String email,
        @NotBlank
        String password) {
}
