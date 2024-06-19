package com.github.anbeld.ForoHub.domain.topico;

import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.curso.CursoRepository;
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
public class TopicoService {

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // Registrar un nuevo tópico
    public Topico registrarTopico(DatosInputTopico datos, UriComponentsBuilder uriComponentsBuilder) {
        // Revisa si existe un usuario que registrado con ese id en la base de datos
        Optional<Usuario> usuarioRegistrado = usuarioRepository.findById(datos.usuario_id());

        if (usuarioRegistrado.isPresent()) {
            // Revisa si existe un curso registrado con ese id en la base de datos
            Optional<Curso> cursoRegistrado = cursoRepository.obtenerCursoPorId(datos.curso_id());

            // Crea y guarda el nuevo tópico en la base de datos
            if (cursoRegistrado.isPresent()) {
                Topico nuevoTopico = new Topico(datos, usuarioRegistrado.get(), cursoRegistrado.get());
                topicoRepository.save(nuevoTopico);

                // Crea la url en base al id del topico
                URI url = uriComponentsBuilder.path("/topicos/{id}")
                        .buildAndExpand(nuevoTopico.getId()).toUri();

                // Codifica la URL antes de guardarla
                String urlEncoded = URLEncoder.encode(String.valueOf(url), StandardCharsets.UTF_8);
                nuevoTopico.setUrl(urlEncoded);

                topicoRepository.save(nuevoTopico); // Agrega la url del nuevo topico en la base de datos
                return nuevoTopico;

            } else {
                throw new ValidacionDeIntegridad("El curso ingresado no es válido");
            }
        } else {
            throw new ValidacionDeIntegridad("El usuario ingresado no es válido");
        }
    }

    // Obtener un listado de tópicos activos
    public Page<DatosOutputTopico> obtenerTopicosActivos(Pageable paginacion) {
        return topicoRepository.obtenerTopicosPorStatus(paginacion, false).map(DatosOutputTopico::new);
    }

    // Obtener el listado de tópicos registrados por status
    public Page<DatosOutputTopico> obtenerTopicosPorStatus(Pageable paginacion, boolean status) {
        return topicoRepository.obtenerTopicosPorStatus(paginacion, status).map(DatosOutputTopico::new);
    }

    // Obtener el listado de tópicos
    public Page<DatosOutputTopico> obtenerTopicos(Pageable paginacion) {
        return topicoRepository.findAll(paginacion).map(DatosOutputTopico::new);
    }

    // Delete logico
    public void cerrarTopico(Long id) {
        // Revisa si existe un topico registrado con ese id y autor en la base de datos
        Optional<Topico> topicoRegistrado = topicoRepository.findById(id);

        if (topicoRegistrado.isPresent()) {
            // Realiza el Delete lógico y actualiza la información en la base de datos
            topicoRegistrado.get().cerrarTopico();
            topicoRepository.save(topicoRegistrado.get());
        } else {
            throw new ValidacionDeIntegridad("El topico ingresado no es válido");
        }
    }
}
