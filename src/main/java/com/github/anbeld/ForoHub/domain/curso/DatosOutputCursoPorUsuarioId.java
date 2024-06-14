package com.github.anbeld.ForoHub.domain.curso;

public record DatosOutputCursoPorUsuarioId(
        Long curso_id,
        String nombre,
        Categoria categoria
) {
    public DatosOutputCursoPorUsuarioId(Curso curso) {
        this(curso.getId(), curso.getNombre(), curso.getCategoria());
    }

}
