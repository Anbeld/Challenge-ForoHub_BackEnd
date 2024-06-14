package com.github.anbeld.ForoHub.domain.usuario;

import com.github.anbeld.ForoHub.infra.errores.ValidacionDeIntegridad;
import com.github.anbeld.ForoHub.infra.security.DatosJWTToken;
import com.github.anbeld.ForoHub.infra.security.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Verificar la información para iniciar sesión
    public DatosJWTToken login(DatosInputLoginUsuario datos) {
        // Verifica si la información que el usuario suministra existe en la base de datos
        var usuarioRegistrado = repository.obtenerUsuarioPorEmail(datos.email());
        if (usuarioRegistrado.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con el email: " + datos.email());
        }

        try {
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    datos.email(), datos.password());
            var usuarioAutenticado = authenticationManager.authenticate(authToken);

            // Recupera el perfil del usuario autenticado
            Perfil perfil = usuarioRegistrado.get().getUserRole();

            var JWTtoken = jwtService.generateToken(datos.email(), perfil);
            return new DatosJWTToken(JWTtoken);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Error en la autenticación", e);
        }
    }

    // Registrar nuevo usuario
    public DatosOutputUsuario registrarUsuario(DatosInputRegistrarUsuario datos, Perfil perfil) {
        var encryptedPassword = passwordEncoder.encode(datos.password());
        Usuario nuevoUsuario = new Usuario(datos, perfil, encryptedPassword);
        repository.save(nuevoUsuario); // Guarda el nuevo usuario en la base de datos
        return new DatosOutputUsuario(nuevoUsuario);
    }

    // Obtener listado de usuarios activos por perfil
    public Page<DatosOutputUsuario> listadoUsuariosActivosPorPerfil(Pageable paginacion, Perfil perfil) {
        // Busca todos los usuarios que cumplan la condición y los conveierte a DatosOutputUsuario para mostrar al usuario
        return repository.findByStatusTrueAndUserRole(paginacion, perfil).map(DatosOutputUsuario::new);
    }

    // Obtener un estudiante por su id
    public DatosOutputEstudiantePorId obtenerEstudiantePorId(Long id) {
        // Revisa si existe un usuario que cumpla las condiciones en la base de datos
        var response = repository.obtenerUsuarioPorIdYPerfil(id, Perfil.ESTUDIANTE);
        if (response.isPresent()) {
            return new DatosOutputEstudiantePorId(response.get());
        } else {
            throw new ValidacionDeIntegridad("Verifique la información suministrada");
        }
    }

    // Obtener un docente por su id
    public DatosOutputDocentePorId obtenerDocentePorId(Long id) {
        // Revisa si existe un usuario que cumpla las condiciones en la base de datos
        var response = repository.obtenerUsuarioPorIdYPerfil(id, Perfil.DOCENTE);
        if (response.isPresent()) {
            return new DatosOutputDocentePorId(response.get());
        } else {
            throw new ValidacionDeIntegridad("Verifique la información suministrada");
        }
    }

    // Actualizar la contraseña de un usuario
    public DatosOutputUsuario actualizarPasswordUsuario(DatosInputActualizarPasswordUsuario datos, Perfil perfil) {
        // Revisa si existe un usuario que cumpla las condiciones en la base de datos
        var response = repository.verificarCorreoPasswordPerfilUsuario(datos.email(), datos.currentPassword(), perfil);
        if (response.isPresent()) {
            // Actualiza la contraseña
            response.get().actualizarPassword(datos);
            return new DatosOutputUsuario(response.get());
        } else {
            // Informa que la información no es correcta
            throw new ValidacionDeIntegridad("Verifique la información suministrada");
        }
    }

    // Delete lógido de un usuario
    public void desactivarUsuario(Long id, Perfil perfil) {
        var response = repository.getReferenceById(id);
        if (response.getUserRole().equals(perfil)) {
            response.desactivarUsuario();
        }
    }
}
