package com.github.anbeld.ForoHub.domain.topico;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.curso.CursoRepository;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TopicoRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    private Topico topicoTest1;
    private Topico topicoTest2;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        Usuario estudianteTest = Usuario.builder()
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .cursosEstudiante(new ArrayList<>())
                .build();

        // Datos de prueba
        Usuario docenteTest = Usuario.builder()
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        usuarioRepository.save(docenteTest);
        usuarioRepository.save(estudianteTest);

        Curso cursoTest1 = Curso.builder()
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        Curso cursoTest2 = Curso.builder()
                .nombre("Curso 2")
                .categoria(Categoria.BACKEND)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        cursoRepository.save(cursoTest1);
        cursoRepository.save(cursoTest2);

        topicoTest1 = Topico.builder()
                .titulo("Tópico test")
                .mensaje("Este es el primer tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .curso(cursoTest1)
                .build();

        topicoTest2 = Topico.builder()
                .titulo("Tópico test2")
                .mensaje("Este es el segundo tópico test")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .curso(cursoTest1)
                .build();
    }

    @DisplayName("Test - Registrar un nuevo curso")
    @Test
    void registrarTopico() {
        // given: Preparación de datos
        // Se asigna el tópico de prueba al objeto nuevoTopico
        Topico nuevoTopico = topicoTest1;

        // when: Ejecución de la acción a probar
        // Se guarda el tópico en el repositorio de tópicos y se obtiene el tópico registrado
        Topico topicoRegistrado = this.topicoRepository.save(nuevoTopico);

        // then: Verificación de los resultados esperados
        // Se verifica que el tópico registrado no sea nulo y que tenga un ID asignado
        assertThat(topicoRegistrado).isNotNull();
        assertThat(topicoRegistrado.getId()).isGreaterThan(0);
    }

    @DisplayName("Test - Obtener listado de tópicos por status")
    @Test
    void obtenerTopicosPorStatus() {
        // given: Preparación de datos
        // Se guarda un tópico activo en el repositorio de tópicos
        topicoRepository.save(topicoTest1);

        // Se cierra un tópico y se guarda en el repositorio de tópicos
        topicoTest2.cerrarTopico();
        topicoRepository.save(topicoTest2);

        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // when: Ejecución de la acción a probar
        // Se obtiene una página de tópicos activos
        Page<Topico> topicosActivos = topicoRepository.obtenerTopicosPorStatus(paginacion, topicoTest1.isResuelto());

        // Se obtiene una página de tópicos cerrados
        Page<Topico> topicosCerrados = topicoRepository.obtenerTopicosPorStatus(paginacion, topicoTest2.isResuelto());

        // then: Verificación de los resultados esperados
        // Se verifica que la página de tópicos activos contenga solo tópicos activos
        assertThat(topicosActivos.getContent()).contains(topicoTest1);
        assertThat(topicosActivos.getContent()).doesNotContain(topicoTest2);

        // Se verifica que la página de tópicos cerrados contenga solo tópicos cerrados
        assertThat(topicosCerrados.getContent()).contains(topicoTest2);
    }

    @DisplayName("Test - Obtener tópicos por id y autor")
    @Test
    void obtenerTopicoPorIdYAutor() {
        // given: Preparación de datos
        // Se guarda un tópico en el repositorio de tópicos
        topicoRepository.save(topicoTest1);
        topicoRepository.save(topicoTest2);

        // when: Ejecución de la acción a probar
        // Se intenta obtener un tópico por su ID y autor (debe existir en el repositorio)
        Optional<Topico> topicoTest1Registrado = topicoRepository.obtenerTopicoPorIdYAutor(topicoTest1.getId(), topicoTest1.getAutor());

        // Se intenta obtener otro tópico por su ID y autor (también debe existir en el repositorio)
        Optional<Topico> topicoTest2Registrado = topicoRepository.obtenerTopicoPorIdYAutor(topicoTest2.getId(), topicoTest2.getAutor());

        // Se intenta obtener un tópico con un ID que existe pero con un autor diferente (no debe existir en el repositorio)
        Optional<Topico> topicoTest2NoRegistrado = topicoRepository.obtenerTopicoPorIdYAutor(topicoTest2.getId(), topicoTest1.getAutor());

        // then: Verificación de los resultados esperados
        // Se verifica que el primer tópico registrado sea igual al tópico original y que tenga el mismo autor
        assertThat(topicoTest1Registrado.get()).isEqualTo(topicoTest1);
        assertThat(topicoTest1Registrado.get().getAutor()).isEqualTo(topicoTest1.getAutor());

        // Se verifica que el segundo tópico registrado sea igual al tópico original y que tenga el mismo autor
        assertThat(topicoTest2Registrado.get()).isEqualTo(topicoTest2);
        assertThat(topicoTest2Registrado.get().getAutor()).isEqualTo(topicoTest2.getAutor());

        // Se verifica que no se haya encontrado un tópico con el ID del segundo tópico y un autor diferente
        assertThat(topicoTest2NoRegistrado).isEqualTo(Optional.empty());
    }
}