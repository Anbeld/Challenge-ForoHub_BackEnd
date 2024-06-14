package com.github.anbeld.ForoHub.domain.usuario;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.anbeld.ForoHub.domain.curso.DatosOutputCursoPorUsuarioId;

import java.util.List;

public record DatosOutputEstudiantePorId(
        @JsonAlias("id")
        Long usuario_id,
        @JsonAlias("userName")
        String nombre,
        String email,
        Perfil perfil,
        List<DatosOutputCursoPorUsuarioId> cursosEstudiante){

    public DatosOutputEstudiantePorId(Usuario usuario) {
        this(usuario.getId(), usuario.getUserName(), usuario.getEmail(), usuario.getUserRole(), usuario.getCursosEstudiante().stream().map(DatosOutputCursoPorUsuarioId::new).toList());
    }
}
