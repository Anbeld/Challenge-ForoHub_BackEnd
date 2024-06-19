package com.github.anbeld.ForoHub.domain.curso;

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

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class CursoRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Curso cursoTest1;
    private Curso cursoTest2;
    private Curso cursoTest3;

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

        cursoTest1 = Curso.builder()
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        cursoTest2 = Curso.builder()
                .nombre("Curso 2")
                .categoria(Categoria.BACKEND)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        cursoTest3 = Curso.builder()
                .nombre("Curso 3")
                .categoria(Categoria.BACKEND)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .build();

        docenteTest.getCursosDocente().add(cursoTest1);
    }

    @DisplayName("Test - Registrar un nuevo curso")
    @Test
    void registrarCurso() {
        // given: Preparación de datos de prueba
        Curso nuevoCurso = cursoTest1;

        // when: Guardar el curso en el repositorio
        Curso cursoRegistrado = this.cursoRepository.save(nuevoCurso);

        // then: Verificación de que el curso se ha registrado correctamente
        assertThat(cursoRegistrado).isNotNull();
        assertThat(cursoRegistrado.getId()).isGreaterThan(0);
    }

    @DisplayName("Test - Obtener listado de cursos asociados al id de un docente")
    @Test
    void obtenerCursosPorIdDocente() {
        // given: Guardar varios cursos en el repositorio
        cursoRepository.save(cursoTest1);
        cursoRepository.save(cursoTest2);
        cursoRepository.save(cursoTest3);

        // Preparación de la paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        // when: Obtener los cursos asociados al id del docente
        Page<Curso> cursos = cursoRepository.obtenerCursosPorIdDocente(paginacion, docenteTest.getId());

        // then: Verificación de que los cursos obtenidos están asociados al docente
        assertThat(cursos.getContent()).contains(cursoTest1);
        assertThat(cursos.getContent()).contains(cursoTest2);
        assertThat(cursos.getContent()).contains(cursoTest3);
    }

    @DisplayName("Test - Obtener listado de cursos asociados al id de un estudiante")
    @Test
    void obtenerCursosPorIdEstudiante() {
        // given: Registrar un estudiante en varios cursos y guardarlos en el repositorio
        cursoTest1.registrarEstudiante(estudianteTest);
        cursoTest2.registrarEstudiante(estudianteTest);

        cursoRepository.save(cursoTest1);
        cursoRepository.save(cursoTest2);
        cursoRepository.save(cursoTest3);
        // Preparación de la paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        // when: Obtener los cursos asociados al id del estudiante
        Page<Curso> cusos = cursoRepository.obtenerCursosPorIdEstudiante(paginacion, estudianteTest.getId());

        // then: Verificación de que los cursos obtenidos están asociados al estudiante
        assertThat(cusos.getContent()).contains(cursoTest1);
        assertThat(cusos.getContent()).contains(cursoTest2);
        assertThat(cusos.getContent()).doesNotContain(cursoTest3);
    }

    @DisplayName("Test - Obtener curso por id")
    @Test
    void obtenerCursoPorId() {
        // given: Guardar un curso en el repositorio
        Curso curso = cursoRepository.save(cursoTest1);
        // Obtener el id del curso registrado
        Long estudianteId = curso.getId();

        // when: Buscar el curso por su id
        Optional<Curso> cursoRegistrado = cursoRepository.findById(estudianteId);

        // then: Verificación de que el curso encontrado es el correcto
        assertThat(cursoRegistrado.get()).isEqualTo(curso);
    }

    @DisplayName("Test - Obtener curso por nombre")
    @Test
    void findByNombre() {
        // given: Guardar un curso en el repositorio
        cursoRepository.save(cursoTest1);
        // Obtener el nombre del curso registrado
        String estudianteEmail = cursoTest1.getNombre();

        // when: Buscar el curso por su nombre
        Optional<Curso> cursoRegistrado = cursoRepository.findByNombre(estudianteEmail);

        // then: Verificación de que el curso encontrado es el correcto
        assertThat(cursoRegistrado.get()).isEqualTo(cursoTest1);
    }
}