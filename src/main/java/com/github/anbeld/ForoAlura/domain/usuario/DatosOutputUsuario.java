package com.github.anbeld.ForoAlura.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.anbeld.ForoAlura.domain.curso.DatosOutputCursoPorUsuarioId;

import java.util.List;

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
