package com.github.anbeld.ForoAlura.domain.respuesta;

import com.github.anbeld.ForoAlura.domain.topico.Topico;
import com.github.anbeld.ForoAlura.domain.topico.TopicoRepository;
import com.github.anbeld.ForoAlura.domain.usuario.Usuario;
import com.github.anbeld.ForoAlura.domain.usuario.UsuarioRepository;
import com.github.anbeld.ForoAlura.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RespuestaService {

    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registrar una nueva respuesta
    public DatosOutputRespuesta registrarRespuesta(DatosInputRespuesta datos) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegistrado = usuarioRepository.obtenerUsuarioPorId(datos.autor_id());

        if (usuarioRegistrado.isPresent()){
            // Revisa si existe un topico que registrado con ese id en la base de datos
            Optional<Topico> topicoRegistrado = topicoRepository.findById(datos.topico_id());

            if (topicoRegistrado.isPresent()){
                // Crea la respuesta y la guarda en la base de datos
                Respuesta nuevaRespuesta = new Respuesta(datos.respuesta(), usuarioRegistrado.get(), topicoRegistrado.get());
                respuestaRepository.save(nuevaRespuesta);
                return new DatosOutputRespuesta(nuevaRespuesta);
            } else {
                throw new ValidacionDeIntegridad("El tópico ingresado no es válido");
            }
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }

    // Obtener un listado de respuestas con tópicos activos
    public Page<DatosOutputRespuesta> obtenerRespuestasPorTopicosActivos(Pageable paginacion) {
        return respuestaRepository.obtenerRespuestasPorTopicosActivos(paginacion).map(DatosOutputRespuesta::new);
    }

    // Obtener un listado de respuestas por autor_id
    public Page<DatosOutputRespuesta> obtenerRespuestasPorAutorId(Pageable paginacion, Long id) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegitrado = usuarioRepository.findById(id);

        if (usuarioRegitrado.isPresent()){
            return respuestaRepository.obtenerRespuestasPorAutor(paginacion, usuarioRegitrado.get()).map(DatosOutputRespuesta::new);
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }

    // Obtener un listado de respuestas por topico_id
    public Page<DatosOutputRespuesta> obtenerRespuestasPorTopicoId(Pageable paginacion, Long id) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Topico> topicoRegitrado = topicoRepository.findById(id);

        if (topicoRegitrado.isPresent()){
            return respuestaRepository.obtenerRespuestasPorTopico(paginacion, topicoRegitrado.get()).map(DatosOutputRespuesta::new);
        } else {
            throw new ValidacionDeIntegridad("El tópico ingresado no es válido");
        }
    }
}
