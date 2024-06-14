package com.github.anbeld.ForoAlura.domain.usuario;

import com.github.anbeld.ForoAlura.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    // Iniciar sesión
    public DatosOutputLoginUsuario login(DatosInputLoginUsuario datos) {
        // Verifica si la información que el usuario suministra exista en la base de datos
        var response = repository.verificarCorreoPasswordUsuarioLogin(datos.email(), datos.password());
        if (response.isPresent()) {
            return new DatosOutputLoginUsuario(response.get());
        } else {
            throw new ValidacionDeIntegridad("Verifique la información suministrada");
        }
    }

    // Registrar nuevo usuario
    public DatosOutputUsuario registrarUsuario(DatosInputRegistrarUsuario datos, Perfil perfil) {
        var response = new Usuario(datos, perfil);
        repository.save(response); // Guarda el nuevo usuario en la base de datos
        return new DatosOutputUsuario(response);
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
