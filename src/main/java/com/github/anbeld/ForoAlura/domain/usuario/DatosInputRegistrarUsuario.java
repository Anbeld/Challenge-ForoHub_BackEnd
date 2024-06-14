package com.github.anbeld.ForoAlura.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record DatosInputRegistrarUsuario(
        @JsonAlias("nombre")
        @NotBlank
        String userName,

        @NotBlank
        String email,
        @NotBlank
        String password) {
}
