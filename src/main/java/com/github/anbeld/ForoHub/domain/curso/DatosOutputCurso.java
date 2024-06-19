package com.github.anbeld.ForoHub.domain.curso;

public record DatosOutputCurso(
        Long curso_id,
        String nombre,
        Categoria categoria,
        String docente,
        Integer numero_estudiantes
) {
    public DatosOutputCurso(Curso curso) {
        this(curso.getId(), curso.getNombre(), curso.getCategoria(), curso.getDocente().getUserName(), curso.getNumeroEstudiantes());
    }
}
