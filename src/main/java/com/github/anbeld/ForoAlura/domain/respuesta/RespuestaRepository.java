package com.github.anbeld.ForoAlura.domain.respuesta;

import com.github.anbeld.ForoAlura.domain.topico.Topico;
import com.github.anbeld.ForoAlura.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    @Query(value = "SELECT r FROM Respuesta r JOIN r.topico t WHERE t.resuelto=false")
    Page<Respuesta> obtenerRespuestasPorTopicosActivos(Pageable paginacion);

    @Query(value = "SELECT r FROM Respuesta r WHERE r.autor=:autor")
    Page<Respuesta> obtenerRespuestasPorAutor(Pageable paginacion, Usuario autor);

    @Query(value = "SELECT r FROM Respuesta r WHERE r.topico=:topico")
    Page<Respuesta> obtenerRespuestasPorTopico(Pageable paginacion, Topico topico);
}
