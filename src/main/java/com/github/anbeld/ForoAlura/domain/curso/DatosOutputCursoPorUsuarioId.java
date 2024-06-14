package com.github.anbeld.ForoAlura.domain.curso;

import com.github.anbeld.ForoAlura.domain.usuario.Usuario;

public record DatosOutputCursoPorUsuarioId(
        Long curso_id,
        String nombre,
        Categoria categoria
) {
    public DatosOutputCursoPorUsuarioId(Curso curso) {
        this(curso.getId(), curso.getNombre(), curso.getCategoria());
    }

}
