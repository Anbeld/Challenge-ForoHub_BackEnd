package com.github.anbeld.ForoHub.domain.respuesta;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.topico.Topico;
import com.github.anbeld.ForoHub.domain.topico.TopicoRepository;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RespuestaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private RespuestaRepository respuestaRepository;

    @InjectMocks
    private RespuestaService service;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Curso cursoTest;
    private Topico topicoTest;
    private Respuesta respuestaTest1;
    private Respuesta respuestaTest2;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        estudianteTest = Usuario.builder()
                .id(1L)
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .cursosEstudiante(new ArrayList<>())
                .build();

        docenteTest = Usuario.builder()
                .id(2L)
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        cursoTest = Curso.builder()
                .id(1L)
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        topicoTest = Topico.builder()
                .id(1L)
                .titulo("Tópico test")
                .mensaje("Este es el primer tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .curso(cursoTest)
                .respuestas(new ArrayList<>())
                .build();

        respuestaTest1 = Respuesta.builder()
                .id(1L)
                .topico(topicoTest)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .respuesta("Esta es la primera respuesta")
                .build();

        respuestaTest2 = Respuesta.builder()
                .id(2L)
                .topico(topicoTest)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .respuesta("Esta es la segunda respuesta")
                .build();
    }

    @DisplayName("Test - Registrar una nueva respuesta")
    @Test
    void registrarRespuesta() {
        // given: Preparación de datos
        // Se configuran las respuestas de los mocks para devolver los datos necesarios
        given(usuarioRepository.findById(docenteTest.getId())).willReturn(Optional.of(docenteTest));
        given(topicoRepository.findById(cursoTest.getId())).willReturn(Optional.of(topicoTest));
        given(respuestaRepository.save(any(Respuesta.class))).willReturn(respuestaTest1);

        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar
        // Se registra una nueva respuesta utilizando el servicio
        Respuesta nuevaRespuesta = service.registrarRespuesta(new DatosInputRespuesta(
                        respuestaTest1.getTopico().getId(),
                        respuestaTest1.getAutor().getId(),
                        respuestaTest1.getRespuesta()),
                testUrl);

        // then: Verificación de los resultados esperados
        // Se verifica que la respuesta registrada no sea nula
        assertThat(nuevaRespuesta).isNotNull();

        // Se verifica que el texto de la respuesta registrada sea igual al texto de la respuesta de prueba
        assertThat(nuevaRespuesta.getRespuesta()).isEqualTo(respuestaTest1.getRespuesta());

        // Se verifica que el autor de la respuesta registrada sea igual al autor de la respuesta de prueba
        assertThat(nuevaRespuesta.getAutor()).isEqualTo(respuestaTest1.getAutor());

        // Se verifica que el tópico de la respuesta registrada sea igual al tópico de la respuesta de prueba
        assertThat(nuevaRespuesta.getTopico()).isEqualTo(respuestaTest1.getTopico());
    }

    @DisplayName("Test - Obtener listado de respuestas por tópicos activos")
    @Test
    void obtenerRespuestasPorTopicosActivos() {
        // given: Preparación de datos
        // Se crea una lista de respuestas y se agrega la respuesta de prueba 1 y 2
        List<Respuesta> respuestas = new ArrayList<>();
        respuestas.add(respuestaTest1);
        respuestas.add(respuestaTest2);

        // Se establece una paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de respuestas para devolver respuestas por tópicos no resueltos
        given(respuestaRepository.obtenerRespuestasPorTopicosActivos(paginacion))
                .willReturn(
                        new PageImpl<>(
                                respuestas.stream().filter(r -> !r.getTopico().isResuelto()).toList(),
                                paginacion,
                                respuestas.size())
                );

        // when: Ejecución de la acción a probar
        // Se llama al servicio para obtener las respuestas por tópicos activos
        var responseTopicoTestAbierto = service.obtenerRespuestasPorTopicosActivos(paginacion);

        // then: Verificación de los resultados esperados
        // Se verifica que la respuesta del servicio no sea nula
        assertThat(responseTopicoTestAbierto).isNotNull();

        // Se verifica que el total de elementos en la respuesta sea igual al tamaño de la lista de respuestas
        assertThat(responseTopicoTestAbierto.getTotalElements()).isEqualTo(respuestas.size());

        // Se verifica que la respuesta del servicio contenga la salida de datos de la respuesta de prueba 1
        assertThat(responseTopicoTestAbierto).contains(new DatosOutputRespuesta(respuestaTest1));

        // Se verifica que la respuesta del servicio contenga la salida de datos de la respuesta de prueba 2
        assertThat(responseTopicoTestAbierto).contains(new DatosOutputRespuesta(respuestaTest2));
    }

    @DisplayName("Test - Obtener listado de respuestas por autor")
    @Test
    void obtenerRespuestasPorAutorId() {
        // given: Preparación de datos
        // Se crea una lista de respuestas y se agrega la respuesta de prueba 1 y 2
        List<Respuesta> respuestas = new ArrayList<>();
        respuestas.add(respuestaTest1);
        respuestas.add(respuestaTest2);

        // Se establece una paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el usuario de prueba estudiante
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.of(estudianteTest));

        // Se configura el comportamiento del mock del repositorio de respuestas para devolver respuestas del estudiante
        given(respuestaRepository.obtenerRespuestasPorAutor(paginacion, estudianteTest))
                .willReturn(
                        new PageImpl<>(
                                respuestas.stream()
                                        .filter(r -> r.getAutor().equals(estudianteTest))
                                        .toList(),
                                paginacion,
                                respuestas.size() - 1)
                );

        // when: Ejecución de la acción a probar
        // Se llama al servicio para obtener las respuestas por autor (ID de estudiante)
        var responseTopicoTestAbierto = service.obtenerRespuestasPorAutorId(paginacion, estudianteTest.getId());

        // then: Verificación de los resultados esperados
        // Se verifica que la respuesta del servicio no sea nula
        assertThat(responseTopicoTestAbierto).isNotNull();

        // Se verifica que el total de elementos en la respuesta sea igual al tamaño de la lista de respuestas menos 1
        assertThat(responseTopicoTestAbierto.getTotalElements()).isEqualTo(respuestas.size() - 1);

        // Se verifica que la respuesta del servicio no contenga la salida de datos de la respuesta de prueba 1
        assertThat(responseTopicoTestAbierto).doesNotContain(new DatosOutputRespuesta(respuestaTest1));

        // Se verifica que la respuesta del servicio contenga la salida de datos de la respuesta de prueba 2
        assertThat(responseTopicoTestAbierto).contains(new DatosOutputRespuesta(respuestaTest2));
    }

    @DisplayName("Test - Obtener listado de respuestas por topico_id")
    @Test
    void obtenerRespuestasPorTopicoId() {
        // given: Preparación de datos
        // Se crea una lista de respuestas y se agrega la respuesta de prueba 1 y 2
        List<Respuesta> respuestas = new ArrayList<>();
        respuestas.add(respuestaTest1);
        respuestas.add(respuestaTest2);

        // Se establece una paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el usuario de prueba estudiante
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.of(estudianteTest));

        // Se configura el comportamiento del mock del repositorio de respuestas para devolver respuestas por ID de tópico
        given(respuestaRepository.obtenerRespuestasPorAutor(paginacion, estudianteTest))
                .willReturn(
                        new PageImpl<>(
                                respuestas.stream()
                                        .filter(r -> r.getTopico().getId().equals(topicoTest.getId()))
                                        .toList(),
                                paginacion,
                                respuestas.size())
                );

        // when: Ejecución de la acción a probar
        // Se llama al servicio para obtener las respuestas por ID de tópico
        var responseTopicoTestAbierto = service.obtenerRespuestasPorAutorId(paginacion, estudianteTest.getId());

        // then: Verificación de los resultados esperados
        // Se verifica que la respuesta del servicio no sea nula
        assertThat(responseTopicoTestAbierto).isNotNull();

        // Se verifica que el total de elementos en la respuesta sea igual al tamaño de la lista de respuestas
        assertThat(responseTopicoTestAbierto.getTotalElements()).isEqualTo(respuestas.size());

        // Se verifica que la respuesta del servicio contenga la salida de datos de la respuesta de prueba 1
        assertThat(responseTopicoTestAbierto).contains(new DatosOutputRespuesta(respuestaTest1));

        // Se verifica que la respuesta del servicio contenga la salida de datos de la respuesta de prueba 2
        assertThat(responseTopicoTestAbierto).contains(new DatosOutputRespuesta(respuestaTest2));
    }
}