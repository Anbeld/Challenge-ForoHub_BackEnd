package com.github.anbeld.ForoHub.infra.security;

import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByEmail(username);
        System.out.println("Revisando aquí: " + username);
        System.out.println("Aquí estoy: " + usuario.getUsername());
        if (usuario == null) {
            throw new UsernameNotFoundException("Email no encontrado: " + username);
        }
        return usuario;
    }
}