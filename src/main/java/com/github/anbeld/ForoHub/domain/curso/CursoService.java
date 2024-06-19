package com.github.anbeld.ForoHub.domain.curso;

import com.github.anbeld.ForoHub.domain.usuario.Perfil;
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
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registrar un curso
    public Curso registrarCurso(DatosInputRegistrarCurso datos, UriComponentsBuilder uriComponentsBuilder) {
        // Revisa si existe un docente que cumpla las condiciones en la base de datos
        Optional<Usuario> docenteVerificado = usuarioRepository.obtenerUsuarioPorIdYPerfil(datos.docente_id(), Perfil.DOCENTE);

        if (docenteVerificado.isPresent()) {
            // Revisa si existe un curso inscrito que ya tenga el nombre ingresado
            Optional<Curso> cursoRegistrado = cursoRepository.findByNombre(datos.nombre());

            // Crea y guarda el nuevo curso en la base de datos
            if (cursoRegistrado.isEmpty()) {
                Curso nuevoCurso = new Curso(datos, docenteVerificado.get());
                cursoRepository.save(nuevoCurso);

                // Crea la url en base al id del curso
                URI url = uriComponentsBuilder.path("/cursos/{id}")
                        .buildAndExpand(nuevoCurso.getId()).toUri();

                // Codifica la URL antes de guardarla
                String urlEncoded = URLEncoder.encode(String.valueOf(url), StandardCharsets.UTF_8);
                nuevoCurso.setUrl(urlEncoded);

                cursoRepository.save(nuevoCurso); // Agrega la url del nuevo curso en la base de datos
                return nuevoCurso;

            } else {
                throw new ValidacionDeIntegridad("El nombre del curso ya está en uso");
            }
        } else {
            throw new ValidacionDeIntegridad("Ingrese un usuario con el rol 'Docente'");
        }
    }

    // Registrar un estudiante a un curso
    public DatosOutputRegistrarEstudianteCurso registrarEstudiante(DatosInputRegistrarEstudianteCurso datos) {
        // Verifica si el usuario ingresado es un estudiante
        Optional<Usuario> estudianteRegistrado = usuarioRepository.obtenerUsuarioPorIdYPerfil(datos.estudiante_id(), Perfil.ESTUDIANTE);

        if (estudianteRegistrado.isPresent()) {
            Optional<Curso> cursoRegistrado = cursoRepository.obtenerCursoPorId(datos.curso_id());
            // Si el usuario es estudiante y el curso suministrado se encuentra registrado, registra el curso al estudiante
            if (cursoRegistrado.isPresent()) {
                // Registra el estudiante y actualiza la base de datos
                cursoRegistrado.get().registrarEstudiante(estudianteRegistrado.get());
                cursoRepository.save(cursoRegistrado.get());
                return new DatosOutputRegistrarEstudianteCurso(estudianteRegistrado.get(), cursoRegistrado.get());
            } else {
                throw new ValidacionDeIntegridad("Ingrese un curso válido");
            }
        } else {
            throw new ValidacionDeIntegridad("Ingrese un estudiante válido");
        }
    }

    // Obtener listado de todos los cursos
    public Page<DatosOutputCurso> obtenerListadoCursos(Pageable paginacion) {
        return cursoRepository.findAll(paginacion).map(DatosOutputCurso::new);
    }

    // Obtener listado de cursos por id y autor
    public Page<DatosOutputCurso> obtenerListadoCursosPorIdUsuario(Pageable paginacion, Long id) {

        Usuario usuario = usuarioRepository.getReferenceById(id);
        // Si es docente, muestra todos los cursos que ha creado
        if (usuario.getUserRole() == Perfil.DOCENTE) {
            Page<Curso> cursos = cursoRepository.obtenerCursosPorIdDocente(paginacion, id);
            return cursos.map(DatosOutputCurso::new);

        // Si es estudiante, muestra todos los cursos en lo que se encuentra registrado
        } else {
            Page<Curso> cursos = cursoRepository.obtenerCursosPorIdEstudiante(paginacion, id);
            return cursos.map(DatosOutputCurso::new);
        }
    }
}
