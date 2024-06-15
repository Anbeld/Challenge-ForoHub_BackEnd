package com.github.anbeld.ForoHub.infra.security;

import jakarta.validation.constraints.NotBlank;

public record DatosJWTToken(@NotBlank String jwTtoken) {
}
