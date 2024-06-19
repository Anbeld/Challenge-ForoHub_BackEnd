package com.github.anbeld.ForoHub.domain.curso;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;

public record DatosOutputRegistrarEstudianteCurso(
        @JsonAlias("userName")
        String nombre_estudiante,
        @JsonAlias("nombre")
        String curso_registrado
) {
    public DatosOutputRegistrarEstudianteCurso(Usuario estudiante, Curso curso) {
        this(estudiante.getUsername(), curso.getNombre());
    }
}
