package com.github.anbeld.ForoAlura.domain.curso;

public record DatosOutputCurso(
        String nombre,
        Categoria categoria,
        String docente,
        Integer numeroEstudiantes
) {
    public DatosOutputCurso(Curso curso) {
        this(curso.getNombre(), curso.getCategoria(), curso.getDocente().getUserName(), curso.getNumeroEstudiantes());
    }
}
