package com.github.anbeld.ForoAlura.domain.topico;

import com.github.anbeld.ForoAlura.domain.curso.Curso;
import com.github.anbeld.ForoAlura.domain.curso.CursoRepository;
import com.github.anbeld.ForoAlura.domain.usuario.Usuario;
import com.github.anbeld.ForoAlura.domain.usuario.UsuarioRepository;
import com.github.anbeld.ForoAlura.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    CursoRepository cursoRepository;

    // Registra un nuevo tópico
    public DatosOutputTopico registrarTopico(DatosInputTopico datos) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegistrado = usuarioRepository.obtenerUsuarioPorId(datos.usuario_id());

        if (usuarioRegistrado.isPresent()) {
            // Revisa si existe un curso registrado con ese id en la base de datos
            Optional<Curso> cursoRegistrado = cursoRepository.obtenerCursoPorId(datos.curso_id());

            // Crea el tópico y lo guarda en la base de datos
            if (cursoRegistrado.isPresent()) {
                Topico nuevoTopico = new Topico(datos, usuarioRegistrado.get(), cursoRegistrado.get());
                topicoRepository.save(nuevoTopico);
                return new DatosOutputTopico(nuevoTopico);
            } else {
                throw new ValidacionDeIntegridad("El curso ingresado no es válido");
            }
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }

    // Obtener un listado de todos los tópicos activos en la base de datos
    public Page<DatosOutputTopico> obtenerTopicosActivos(Pageable paginacion) {
        return topicoRepository.obtenerTopicosPorStatus(paginacion, false).map(DatosOutputTopico::new);
    }

    // Obtener un listado de todos los tópicos con el status suministrado
    public Page<DatosOutputTopico> obtenerTopicosPorStatus(Pageable paginacion, boolean status) {
        return topicoRepository.obtenerTopicosPorStatus(paginacion, status).map(DatosOutputTopico::new);
    }

    // Obtener un listado de todos los tópicos registrados en la base de datos
    public Page<DatosOutputTopico> obtenerTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(DatosOutputTopico::new);
    }

    // Delete logico
    public void cerrarTopico(DatosInputDeleteTopico datos) {
        // Revisa si existe un usuario registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegistrado = usuarioRepository.obtenerUsuarioPorId(datos.autor_id());

        if (usuarioRegistrado.isPresent()) {

            // Revisa si existe un topico registrado con ese id y autor en la base de datos
            Optional<Topico> topicoRegistrado = topicoRepository.obtenerTopicoPorIdYAutor(datos.topico_id(), usuarioRegistrado.get());

            if (topicoRegistrado.isPresent()) {
                // Realiza el Delete lógico y actualiza la información en la base de datos
                topicoRegistrado.get().cerrarTopico();
                topicoRepository.save(topicoRegistrado.get());
            } else {
                throw new ValidacionDeIntegridad("El topico ingresado no es válido");
            }
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }
}
