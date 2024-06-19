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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

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
    public Usuario registrarUsuario(DatosInputRegistrarUsuario datos, Perfil perfil, UriComponentsBuilder uriComponentsBuilder) {
        // Revisa si existe un usuario inscrito que ya tenga el email ingresado
        Optional<Usuario> usuarioRegistrado = repository.obtenerUsuarioPorEmail(datos.email());

        // Crea y guarda el nuevo usuario en la base de datos
        if (usuarioRegistrado.isEmpty()) {
            // Encripta la contraseña antes de guardarla
            var encryptedPassword = passwordEncoder.encode(datos.password());

            Usuario nuevoUsuario = new Usuario(datos, perfil, encryptedPassword);
            repository.save(nuevoUsuario); // Guarda el nuevo usuario en la base de datos

            // Crea la url en base al perfil y al id del usuario
            URI url = uriComponentsBuilder.path("/{perfil}/{id}")
                    .buildAndExpand(
                            nuevoUsuario.getUserRole().toString().toLowerCase() + "s",
                            nuevoUsuario.getId()).toUri();

            // Codifica la URL antes de guardarla
            String urlEncoded = URLEncoder.encode(String.valueOf(url), StandardCharsets.UTF_8);
            nuevoUsuario.setUrl(urlEncoded);

            repository.save(nuevoUsuario); // Agrega la url del nuevo usuario en la base de datos
            return nuevoUsuario;

        } else {
            throw new ValidacionDeIntegridad("El email " + datos.email() + " ya está en uso");
        }
    }

    // Obtener listado de usuarios activos por perfil
    public Page<DatosOutputUsuario> listadoUsuariosActivosPorPerfil(Pageable paginacion, Perfil perfil) {
        // Busca todos los usuarios que cumplan la condición y los conveierte a DatosOutputUsuario para mostrar al usuario
        return repository.obtenerUsuariosPorStatusActivoYPerfil(paginacion, perfil).map(DatosOutputUsuario::new);
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
        var response = repository.verificarUsuarioPorEmailPasswordPerfil(datos.email(), datos.current_password(), perfil);
        if (response.isPresent()) {
            // Encripta la nueva contraseña antes de guardarla
            var encryptedPassword = passwordEncoder.encode(datos.new_password());

            // Actualiza la contraseña
            response.get().actualizarPassword(encryptedPassword);
            return new DatosOutputUsuario(response.get());

        } else {
            // Informa que la información no es correcta
            throw new ValidacionDeIntegridad("Verifique la información suministrada");
        }
    }

    // Delete lógido de un usuario
    public void desactivarUsuario(Long id, Perfil perfil) {
        Usuario usuarioRegistrado = repository.getReferenceById(id);
        if (usuarioRegistrado.getUserRole().equals(perfil)) {
            usuarioRegistrado.desactivarUsuario();
            repository.save(usuarioRegistrado);
        }
    }
}
