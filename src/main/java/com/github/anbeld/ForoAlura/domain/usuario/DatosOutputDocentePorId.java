package com.github.anbeld.ForoAlura.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.anbeld.ForoAlura.domain.curso.DatosOutputCursoPorUsuarioId;

import java.util.List;

public record DatosOutputDocentePorId(
        @JsonAlias("id")
        Long usuario_id,
        @JsonAlias("userName")
        String nombre,
        String email,
        Perfil perfil,
        List<DatosOutputCursoPorUsuarioId> cursosDocente){

    public DatosOutputDocentePorId(Usuario usuario) {
        this(usuario.getId(), usuario.getUserName(), usuario.getEmail(), usuario.getUserRole(), usuario.getCursosDocente().stream().map(DatosOutputCursoPorUsuarioId::new).toList());
    }
}
