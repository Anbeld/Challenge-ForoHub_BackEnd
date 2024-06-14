package com.github.anbeld.ForoHub.domain.curso;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;

public record DatosOutputRegistrarEstudianteCurso(
        @JsonAlias("userName")
        String nombreEstudiante,
        @JsonAlias("nombre")
        String cursoRegistrado
) {
    public DatosOutputRegistrarEstudianteCurso(Usuario estudiante, Curso curso) {
        this(estudiante.getUserName(), curso.getNombre());
    }
}
