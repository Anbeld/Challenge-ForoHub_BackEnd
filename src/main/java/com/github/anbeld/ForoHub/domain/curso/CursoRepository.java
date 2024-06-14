package com.github.anbeld.ForoHub.domain.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    @Query(value = "SELECT c FROM Curso c WHERE c.docente.id=:id")
    Page<Curso> obtenerCursosPorIdDocente(Pageable paginacion, Long id);

    @Query(value = "SELECT c FROM Curso c JOIN c.estudiantes e WHERE e.id=:id")
    Page<Curso> obtenerCursosPorIdEstudiante(Pageable paginacion, Long id);

    @Query(value = "SELECT c FROM Curso c WHERE c.id=:id")
    Optional<Curso> obtenerCursoPorId(Long id);

    Optional<Curso> findByNombre(String nombre);
}

