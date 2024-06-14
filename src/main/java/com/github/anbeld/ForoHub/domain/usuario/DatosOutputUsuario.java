package com.github.anbeld.ForoHub.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosOutputUsuario(
        @JsonAlias("id")
        Long usuario_id,
        @JsonAlias("userName")
        String nombre,
        String email,
        Perfil perfil){

    public DatosOutputUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getUserName(), usuario.getEmail(), usuario.getUserRole());
    }
}
