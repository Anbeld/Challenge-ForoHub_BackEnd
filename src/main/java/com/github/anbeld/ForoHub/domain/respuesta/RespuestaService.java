package com.github.anbeld.ForoHub.domain.respuesta;

import com.github.anbeld.ForoHub.domain.topico.Topico;
import com.github.anbeld.ForoHub.domain.topico.TopicoRepository;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import com.github.anbeld.ForoHub.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
    public Respuesta registrarRespuesta(DatosInputRespuesta datos, UriComponentsBuilder uriComponentsBuilder) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegistrado = usuarioRepository.obtenerUsuarioPorId(datos.autor_id());

        if (usuarioRegistrado.isPresent()) {
            // Revisa si existe un topico que registrado con ese id en la base de datos
            Optional<Topico> topicoRegistrado = topicoRepository.findById(datos.topico_id());

            // Crea y guarda la nueva respuesta en la base de datos
            if (topicoRegistrado.isPresent()) {
                Respuesta nuevaRespuesta = new Respuesta(datos.respuesta(), usuarioRegistrado.get(), topicoRegistrado.get());
                respuestaRepository.save(nuevaRespuesta);

                // Crea la url en base al id de la respuesta
                URI url = uriComponentsBuilder.path("/respuestas/{id}")
                        .buildAndExpand(nuevaRespuesta.getId()).toUri();

                // Codifica la URL antes de guardarla
                String urlEncoded = URLEncoder.encode(String.valueOf(url), StandardCharsets.UTF_8);
                nuevaRespuesta.setUrl(urlEncoded);

                respuestaRepository.save(nuevaRespuesta); // Agrega la url de la nueva respuesta en la base de datos
                return nuevaRespuesta;

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

        if (usuarioRegitrado.isPresent()) {
            return respuestaRepository.obtenerRespuestasPorAutor(paginacion, usuarioRegitrado.get()).map(DatosOutputRespuesta::new);
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }

    // Obtener un listado de respuestas por topico_id
    public Page<DatosOutputRespuesta> obtenerRespuestasPorTopicoId(Pageable paginacion, Long id) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Topico> topicoRegitrado = topicoRepository.findById(id);

        if (topicoRegitrado.isPresent()) {
            return respuestaRepository.obtenerRespuestasPorTopico(paginacion, topicoRegitrado.get()).map(DatosOutputRespuesta::new);
        } else {
            throw new ValidacionDeIntegridad("El tópico ingresado no es válido");
        }
    }
}
