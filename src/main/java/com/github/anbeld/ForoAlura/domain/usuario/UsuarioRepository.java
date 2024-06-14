package com.github.anbeld.ForoAlura.domain.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT s FROM Usuario s WHERE s.email=:email AND s.password=:password")
    Optional<Usuario> verificarCorreoPasswordUsuarioLogin(String email, String password);

    @Query(value = "SELECT s FROM Usuario s WHERE s.status=true AND s.userRole=:perfil")
    Page<Usuario> findByStatusTrueAndUserRole(Pageable paginacion, Perfil perfil);

    @Query(value = "SELECT s FROM Usuario s WHERE s.id=:id AND s.userRole=:perfil")
    Optional<Usuario> obtenerUsuarioPorIdYPerfil(Long id, Perfil perfil);

    @Query(value = "SELECT s FROM Usuario s WHERE s.email=:email AND s.password=:password AND s.userRole=:perfil")
    Optional<Usuario> verificarCorreoPasswordPerfilUsuario(String email, String password, Perfil perfil);

    @Query(value = "SELECT s FROM Usuario s WHERE s.id=:id")
    Optional<Usuario> obtenerUsuarioPorId(Long id);
}
