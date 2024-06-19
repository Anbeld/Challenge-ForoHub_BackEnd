package com.github.anbeld.ForoHub.domain.respuesta;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.curso.CursoRepository;
import com.github.anbeld.ForoHub.domain.topico.Topico;
import com.github.anbeld.ForoHub.domain.topico.TopicoRepository;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RespuestaRepositoryTest {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private RespuestaRepository respuestaRepository;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Topico topicoTest;
    private Respuesta respuestaTest1;
    private Respuesta respuestaTest2;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        estudianteTest = Usuario.builder()
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .cursosEstudiante(new ArrayList<>())
                .build();

        docenteTest = Usuario.builder()
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        usuarioRepository.save(docenteTest);
        usuarioRepository.save(estudianteTest);

        Curso cursoTest = Curso.builder()
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        cursoRepository.save(cursoTest);

        topicoTest = Topico.builder()
                .titulo("Tópico test")
                .mensaje("Este es el primer tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .curso(cursoTest)
                .build();

        topicoRepository.save(topicoTest);

        respuestaTest1 = Respuesta.builder()
                .topico(topicoTest)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .respuesta("Esta es la primera respuesta")
                .build();

        respuestaTest2 = Respuesta.builder()
                .topico(topicoTest)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .respuesta("Esta es la segunda respuesta")
                .build();
    }

    @DisplayName("Test - Registrar una nueva respuesta")
    @Test
    void registrarRespuesta() {
        // given: Preparación de los datos de prueba
        // Se crea una nueva respuesta utilizando una instancia de prueba existente
        Respuesta nuevaRespuesta = respuestaTest1;

        // when: Ejecución del método a probar, que guarda la nueva respuesta en el repositorio
        // Se guarda la nueva respuesta en el repositorio y se obtiene la respuesta registrada
        Respuesta respuestaRegistrada = this.respuestaRepository.save(nuevaRespuesta);

        // then: Verificación de los resultados esperados
        assertThat(respuestaRegistrada).isNotNull();
        assertThat(respuestaRegistrada.getId()).isGreaterThan(0);
    }

    @DisplayName("Test - Obtener un listado de respuestas por tópicos activos")
    @Test
    void obtenerRespuestasPorTopicosActivos() {
        // given: Preparación de los datos de prueba
        // Se guardan dos respuestas de prueba en el repositorio de respuestas
        respuestaRepository.save(respuestaTest1);
        respuestaRepository.save(respuestaTest2);

        // Se define la paginación para la consulta de respuestas por tópicos activos
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // when: Ejecución de la consulta de respuestas por tópicos activos
        // Se realiza la primera consulta para obtener respuestas por tópicos activos
        var response1 = respuestaRepository.obtenerRespuestasPorTopicosActivos(paginacion);

        // Se cierra el tópico asociado a una de las respuestas para que sea inactivo
        topicoTest.cerrarTopico();
        topicoRepository.save(topicoTest);

        // Se realiza la segunda consulta después de cerrar el tópico
        var response2 = respuestaRepository.obtenerRespuestasPorTopicosActivos(paginacion);

        //then: Verificación de los resultados esperados
        // Se verifica que la primera respuesta esté presente en la primera consulta
        assertThat(response1).isNotNull();
        assertThat(response1.getContent()).contains(respuestaTest1);
        assertThat(response1.getContent()).contains(respuestaTest2);

        // Se verifica que las respuestas no estén presentes en la segunda consulta después de cerrar el tópico
        assertThat(response2.getContent()).doesNotContain(respuestaTest1);
        assertThat(response2.getContent()).doesNotContain(respuestaTest2);
    }

    @DisplayName("Test - Obtener un listado de respuestas por autor")
    @Test
    void obtenerRespuestasPorAutor() {
        // given: Preparación de los datos de prueba
        // Se guardan dos respuestas de prueba en el repositorio de respuestas
        respuestaRepository.save(respuestaTest1);
        respuestaRepository.save(respuestaTest2);

        // Se define la paginación para la consulta de respuestas por autor
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // when: Ejecución de la consulta de respuestas por autor
        // Se realiza la consulta para obtener respuestas por autor (docente)
        var respuestasDocente = respuestaRepository.obtenerRespuestasPorAutor(paginacion, docenteTest);

        // Se realiza la consulta para obtener respuestas por autor (estudiante)
        var respuestasEstudiante = respuestaRepository.obtenerRespuestasPorAutor(paginacion, estudianteTest);

        // then: Verificación de los resultados esperados
        // Se verifica que la consulta para respuestas del docente devuelva la respuesta del docente
        assertThat(respuestasDocente).isNotNull();
        assertThat(respuestasDocente.getTotalElements()).isEqualTo(1);
        assertThat(respuestasDocente.getContent()).contains(respuestaTest1);

        // Se verifica que la consulta para respuestas del estudiante devuelva la respuesta del estudiante
        assertThat(respuestasEstudiante).isNotNull();
        assertThat(respuestasEstudiante.getTotalElements()).isEqualTo(1);
        assertThat(respuestasEstudiante.getContent()).contains(respuestaTest2);
    }

    @DisplayName("Test - Obtener un listado de respuestas por tópico")
    @Test
    void obtenerRespuestasPorTopico() {
        // given: Preparación de los datos de prueba
        // Se guardan dos respuestas de prueba en el repositorio de respuestas
        respuestaRepository.save(respuestaTest1);
        respuestaRepository.save(respuestaTest2);

        // Se define la paginación para la consulta de respuestas por tópico
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // when: Ejecución de la consulta de respuestas por tópico
        // Se realiza la consulta para obtener respuestas por tópico
        var response = respuestaRepository.obtenerRespuestasPorTopico(paginacion, topicoTest);

        // then: Verificación de los resultados esperados
        // Se verifica que la respuesta obtenida no sea nula y que contenga las respuestas de prueba
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getContent()).contains(respuestaTest1);
        assertThat(response.getContent()).contains(respuestaTest2);
    }
}