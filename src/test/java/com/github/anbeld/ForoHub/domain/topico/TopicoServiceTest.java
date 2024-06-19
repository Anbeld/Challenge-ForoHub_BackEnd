package com.github.anbeld.ForoHub.domain.topico;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.curso.CursoRepository;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import com.github.anbeld.ForoHub.infra.errores.ValidacionDeIntegridad;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TopicoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private TopicoService service;

    private Usuario estudianteTest;
    private Curso cursoTest;
    private Topico topicoTest1;
    private Topico topicoTest2;
    private Topico topicoTest3;

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

        // Datos de prueba
        Usuario docenteTest = Usuario.builder()
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

        topicoTest1 = Topico.builder()
                .titulo("Tópico test")
                .mensaje("Este es el primer tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .curso(cursoTest)
                .build();

        topicoTest2 = Topico.builder()
                .titulo("Tópico test2")
                .mensaje("Este es el segundo tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .curso(cursoTest)
                .build();

        topicoTest3 = Topico.builder()
                .titulo("Tópico test3")
                .mensaje("Este es el tercer tópico test")
                .resuelto(true)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .curso(cursoTest)
                .build();
    }

    @DisplayName("Test - Registrar un nuevo tópico")
    @Test
    void registrarTopico() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el usuario de prueba estudiante
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.of(estudianteTest));

        // Se configura el comportamiento del mock del repositorio de cursos para devolver el curso de prueba
        given(cursoRepository.obtenerCursoPorId(cursoTest.getId())).willReturn(Optional.of(cursoTest));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver el tópico de prueba
        given(topicoRepository.save(any(Topico.class))).willReturn(topicoTest1);

        // Se construye una URL de prueba para simular la solicitud HTTP
        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar
        // Se llama al servicio para registrar un nuevo tópico con los datos de entrada
        Topico nuevoTopico = service.registrarTopico(new DatosInputTopico(
                        topicoTest1.getCurso().getId(),
                        topicoTest1.getAutor().getId(),
                        topicoTest1.getTitulo(),
                        topicoTest1.getMensaje()),
                testUrl);

        // then: Verificación de los resultados esperados
        // Se verifica que el tópico devuelto por el servicio no sea nulo y sea igual al tópico de prueba
        assertThat(nuevoTopico).isNotNull();
        assertThat(nuevoTopico).isEqualTo(topicoTest1);
    }

    @DisplayName("Test - Registrar un nuevo tópico con ValidacionDeIntegridad usuario no válido")
    @Test
    void registrarTopicoConValidacionIntegridadUsuarioNoValido() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un Optional vacío, simulando un usuario no válido
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.empty());

        // Se construye una URL de prueba para simular la solicitud HTTP
        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar y verificación de la excepción esperada
        // Se utiliza assertThrows para verificar que se lance una excepción de tipo ValidacionDeIntegridad al intentar registrar el tópico,
        // debido a que el usuario no existe
        assertThrows(ValidacionDeIntegridad.class, () -> service.registrarTopico(new DatosInputTopico(
                        topicoTest1.getCurso().getId(),
                        topicoTest1.getAutor().getId(),
                        topicoTest1.getTitulo(),
                        topicoTest1.getMensaje()),
                testUrl));

        // then: Verificación de que no se llamó al método save del repositorio de tópicos
        // Se verifica que el método save del repositorio de tópicos nunca fue llamado
        verify(topicoRepository, never()).save(any(Topico.class));
    }

    @DisplayName("Test - Registrar un nuevo tópico con ValidacionDeIntegridad curso no válido")
    @Test
    void registrarTopicoConValidacionIntegridadCursoNoValido() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un Optional con el usuario válido
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.of(estudianteTest));

        // Se configura el comportamiento del mock del repositorio de cursos para devolver un Optional vacío, simulando un curso no válido
        given(cursoRepository.obtenerCursoPorId(cursoTest.getId())).willReturn(Optional.empty());

        // Se construye una URL de prueba para simular la solicitud HTTP
        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar y verificación de la excepción esperada
        // Se utiliza assertThrows para verificar que se lance una excepción de tipo ValidacionDeIntegridad al intentar registrar el tópico,
        // debido a que el curso no exite
        assertThrows(ValidacionDeIntegridad.class, () -> service.registrarTopico(new DatosInputTopico(
                        topicoTest1.getCurso().getId(),
                        topicoTest1.getAutor().getId(),
                        topicoTest1.getTitulo(),
                        topicoTest1.getMensaje()),
                testUrl));

        // then: Verificación de que no se llamó al método save del repositorio de tópicos
        // Se verifica que el método save del repositorio de tópicos nunca fue llamado
        verify(topicoRepository, never()).save(any(Topico.class));
    }

    @DisplayName("Test - Obtener listado de tópicos activos")
    @Test
    void obtenerTopicosActivos() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se prepara una lista de tópicos activos
        List<Topico> topicos = new ArrayList<>();
        topicos.add(topicoTest1);
        topicos.add(topicoTest2);
        topicos.add(topicoTest3);

        // Se establece la paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver un Page con los tópicos activos
        given(topicoRepository.obtenerTopicosPorStatus(paginacion, false))
                .willReturn(
                        new PageImpl<>(
                                topicos.stream().filter(t -> !t.isResuelto()).toList(), // Filtrar tópicos activos
                                paginacion,
                                topicos.size())
                );

        // when: Ejecución de la acción a probar
        // Se obtiene la respuesta del servicio al obtener los tópicos activos
        var response = service.obtenerTopicosActivos(paginacion);

        //then: Verificación de los resultados esperados
        // Se verifica que la respuesta no sea nula y que contenga la cantidad correcta de elementos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(topicos.size() - 1); // Se resta 1 porque uno de los tópicos es resuelto
        // Se verifica que la respuesta contenga los tópicos activos esperados y no contenga el tópico resuelto
        assertThat(response).contains(new DatosOutputTopico(topicoTest1));
        assertThat(response).contains(new DatosOutputTopico(topicoTest2));
        assertThat(response).doesNotContain(new DatosOutputTopico(topicoTest3));
    }

    @DisplayName("Test - Obtener listado de tópicos inactivos")
    @Test
    void obtenerTopicosPorStatusTrue() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se prepara una lista de tópicos
        List<Topico> topicos = new ArrayList<>();
        topicos.add(topicoTest1);
        topicos.add(topicoTest2);
        topicos.add(topicoTest3);

        // Se establece la paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver un Page con los tópicos inactivos
        given(topicoRepository.obtenerTopicosPorStatus(paginacion, true))
                .willReturn(
                        new PageImpl<>(
                                topicos.stream().filter(Topico::isResuelto).toList(), // Filtrar tópicos inactivos
                                paginacion,
                                topicos.size())
                );

        // when: Ejecución de la acción a probar
        // Se obtiene la respuesta del servicio al obtener los tópicos inactivos
        var response = service.obtenerTopicosPorStatus(paginacion, true);

        //then: Verificación de los resultados esperados
        // Se verifica que la respuesta no sea nula y que contenga la cantidad correcta de elementos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(topicos.size() - 2); // Se resta 2 porque dos de los tópicos son resueltos
        // Se verifica que la respuesta contenga el tópico inactivo esperado y no contenga los tópicos activos
        assertThat(response).doesNotContain(new DatosOutputTopico(topicoTest1));
        assertThat(response).doesNotContain(new DatosOutputTopico(topicoTest2));
        assertThat(response).contains(new DatosOutputTopico(topicoTest3));
    }

    @DisplayName("Test - Obtener listado de todos los tópicos")
    @Test
    void obtenerTopicos() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se prepara una lista de tópicos
        List<Topico> topicos = new ArrayList<>();
        topicos.add(topicoTest1);
        topicos.add(topicoTest2);
        topicos.add(topicoTest3);

        // Se establece la paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver un Page con todos los tópicos
        given(topicoRepository.obtenerTopicosPorStatus(paginacion, true))
                .willReturn(
                        new PageImpl<>(topicos, paginacion, topicos.size())
                );

        // when: Ejecución de la acción a probar
        // Se obtiene la respuesta del servicio al obtener todos los tópicos
        var response = service.obtenerTopicosPorStatus(paginacion, true);

        //then: Verificación de los resultados esperados
        // Se verifica que la respuesta no sea nula y que contenga la cantidad correcta de elementos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(topicos.size());
        // Se verifica que la respuesta contenga todos los tópicos esperados
        assertThat(response).contains(new DatosOutputTopico(topicoTest1));
        assertThat(response).contains(new DatosOutputTopico(topicoTest2));
        assertThat(response).contains(new DatosOutputTopico(topicoTest3));
    }

    @DisplayName("Test - Cerrar un tópico")
    @Test
    void cerrarTopico() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se prepara una lista de tópicos activos
        List<Topico> topicos = new ArrayList<>();
        topicos.add(topicoTest2);
        topicos.add(topicoTest3);

        // Se establece la paginación para la consulta
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el estudiante
        given(usuarioRepository.findById(estudianteTest.getId())).willReturn(Optional.of(estudianteTest));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver el tópico que se va a cerrar
        given(topicoRepository.findById(topicoTest1.getId())).willReturn(Optional.of(topicoTest1));

        // Se configura el comportamiento del mock del repositorio de tópicos para devolver una lista de tópicos activos
        given(topicoRepository.obtenerTopicosPorStatus(paginacion, false))
                .willReturn(
                        new PageImpl<>(
                                topicos.stream().filter(t -> !t.isResuelto()).toList(),
                                paginacion,
                                topicos.size())
                );
        // Se configura el comportamiento del mock del repositorio de tópicos para devolver el tópico actualizado
        given(topicoRepository.save(any(Topico.class))).willReturn(topicoTest1);

        //when: Ejecución de la acción a probar
        // Se cierra el tópico
        service.cerrarTopico(topicoTest1.getId());
        topicos.add(topicoTest1);

        // Se obtiene la lista de tópicos activos después de cerrar el tópico
        var response = service.obtenerTopicosPorStatus(paginacion, false);

        //then: Verificación de los resultados esperados
        // Se verifica que el tópico se haya cerrado correctamente
        assertThat(topicoTest1.isResuelto()).isEqualTo(true);
        // Se verifica que la respuesta no sea nula y que contenga la cantidad correcta de elementos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(topicos.size() - 2);
        // Se verifica que la respuesta contenga los tópicos esperados
        assertThat(response).doesNotContain(new DatosOutputTopico(topicoTest1));
        assertThat(response).contains(new DatosOutputTopico(topicoTest2));
        assertThat(response).doesNotContain(new DatosOutputTopico(topicoTest3));
    }
}