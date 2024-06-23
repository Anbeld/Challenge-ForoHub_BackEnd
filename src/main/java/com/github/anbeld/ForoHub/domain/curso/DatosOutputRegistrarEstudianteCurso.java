package com.github.anbeld.ForoHub.domain.curso;

import com.github.anbeld.ForoHub.domain.usuario.Usuario;

public record DatosOutputRegistrarEstudianteCurso(
        String nombre_estudiante,
        String curso_registrado
) {
    public DatosOutputRegistrarEstudianteCurso(Usuario estudiante, Curso curso) {
        this(estudiante.getUsername(), curso.getNombre());
    }
}
