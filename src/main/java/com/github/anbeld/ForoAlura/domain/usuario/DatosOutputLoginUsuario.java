package com.github.anbeld.ForoAlura.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosOutputLoginUsuario(
        @JsonAlias("id")
        Long usuario_id,
        @JsonAlias("userName")
        String nombre,
        Perfil perfil){

    public DatosOutputLoginUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getUserName(), usuario.getUserRole());
    }
}
