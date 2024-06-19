package com.github.anbeld.ForoHub.domain.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT s FROM Usuario s WHERE s.status=true AND s.userRole=:perfil")
    Page<Usuario> obtenerUsuariosPorStatusActivoYPerfil(Pageable paginacion, Perfil perfil);

    @Query(value = "SELECT s FROM Usuario s WHERE s.id=:id AND s.userRole=:perfil")
    Optional<Usuario> obtenerUsuarioPorIdYPerfil(Long id, Perfil perfil);

    @Query(value = "SELECT s FROM Usuario s WHERE s.email=:email AND s.password=:password AND s.userRole=:perfil")
    Optional<Usuario> verificarUsuarioPorEmailPasswordPerfil(String email, String password, Perfil perfil);

    UserDetails findByEmail(String email);

    @Query(value = "SELECT s FROM Usuario s WHERE s.email=:email")
    Optional<Usuario> obtenerUsuarioPorEmail(String email);
}
