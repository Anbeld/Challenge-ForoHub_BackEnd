package com.github.anbeld.ForoHub.domain.topico;

import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    @Query(value = "SELECT t FROM Topico t WHERE t.resuelto=:status")
    Page<Topico> obtenerTopicosPorStatus(Pageable paginacion, boolean status);

    @Query(value = "SELECT t FROM Topico t WHERE t.id=:topico_id AND t.autor=:autor")
    Optional<Topico> obtenerTopicoPorIdYAutor(Long topico_id, Usuario autor);
}
