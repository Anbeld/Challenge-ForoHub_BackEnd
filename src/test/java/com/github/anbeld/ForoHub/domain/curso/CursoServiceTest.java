package com.github.anbeld.ForoHub.domain.curso;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CursoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService service;

    // Datos de prueba
    private Usuario docenteTest1;
    private Usuario estudianteTest;
    private Curso cursoTest1;
    private Curso cursoTest2;
    private Curso cursoTest3;
    private Curso cursoTest4;
    private Curso cursoTest5;

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

        docenteTest1 = Usuario.builder()
                .id(2L)
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        Usuario docenteTest2 = Usuario.builder()
                .id(3L)
                .userName("Fernando Docente2")
                .email("fernando.docente2@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        cursoTest1 = Curso.builder()
                .id(1L)
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest1)
                .estudiantes(new ArrayList<>())
                .build();

        cursoTest2 = Curso.builder()
                .id(2L)
                .nombre("Curso 2")
                .categoria(Categoria.BACKEND)
                .numeroEstudiantes(0)
                .docente(docenteTest1)
                .estudiantes(new ArrayList<>())
                .build();

        cursoTest3 = Curso.builder()
                .id(3L)
                .nombre("Curso 3")
                .categoria(Categoria.BACKEND)
                .numeroEstudiantes(0)
                .docente(docenteTest1)
                .estudiantes(new ArrayList<>())
                .build();


        cursoTest4 = Curso.builder()
                .id(4L)
                .nombre("Curso 4")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest2)
                .estudiantes(new ArrayList<>())
                .build();

        cursoTest5 = Curso.builder()
                .id(5L)
                .nombre("Curso 5")
                .categoria(Categoria.FRONTEND)
                .numeroEstudiantes(0)
                .docente(docenteTest1)
                .estudiantes(new ArrayList<>())
                .build();
    }

    @DisplayName("Test - Registrar un nuevo curso")
    @Test
    void registrarCurso() {
        // given: Configuración de condiciones iniciales y datos de prueba
        // Se simula que se obtiene un docente existente por su ID y perfil
        given(usuarioRepository.obtenerUsuarioPorIdYPerfil(docenteTest1.getId(), docenteTest1.getUserRole())).willReturn(Optional.of(docenteTest1));

        // Se simula que no existe un curso con el mismo nombre en el repositorio
        given(cursoRepository.findByNombre(cursoTest1.getNombre())).willReturn(Optional.empty());

        // Se simula el guardado del curso en el repositorio, devolviendo el curso de prueba
        given(cursoRepository.save(any(Curso.class))).willReturn(cursoTest1);

        // Construcción de la URL base para la prueba
        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución del método a probar, que registra un nuevo curso
        Curso nuevoCurso = service.registrarCurso(
                new DatosInputRegistrarCurso(
                        cursoTest1.getNombre(),
                        cursoTest1.getCategoria(),
                        cursoTest1.getDocente().getId()
                ),
                testUrl
        );

        // then: Verificación de los resultados esperados
        assertThat(nuevoCurso).isNotNull();
        assertThat(nuevoCurso.getNombre()).isEqualTo(cursoTest1.getNombre());
        assertThat(nuevoCurso.getCategoria()).isEqualTo(cursoTest1.getCategoria());
        assertThat(nuevoCurso.getDocente()).isEqualTo(cursoTest1.getDocente());
    }

    @DisplayName("Test - Registrar un estudiante a un curso existente")
    @Test
    void registrarEstudiante() {
        // given: Configuración de condiciones iniciales y datos de prueba
        // Simula la obtención de un estudiante existente por su ID y perfil
        given(usuarioRepository.obtenerUsuarioPorIdYPerfil(estudianteTest.getId(), estudianteTest.getUserRole())).willReturn(Optional.of(estudianteTest));

        // Simula la obtención de un curso existente por su ID
        given(cursoRepository.obtenerCursoPorId(cursoTest1.getId())).willReturn(Optional.of(cursoTest1));

        // Simula el guardado del curso en el repositorio, devolviendo el curso de prueba
        given(cursoRepository.save(any(Curso.class))).willReturn(cursoTest1);

        // when: Ejecución del método a probar, que registra un estudiante en un curso
        DatosOutputRegistrarEstudianteCurso datos = service.registrarEstudiante(
                new DatosInputRegistrarEstudianteCurso(
                        cursoTest1.getId(),
                        estudianteTest.getId()
                )
        );

        // then: Verificación de los resultados esperados
        assertThat(estudianteTest.getCursosEstudiante()).contains(cursoTest1);
        assertThat(cursoTest1.getEstudiantes()).contains(estudianteTest);
        assertThat(cursoTest1.getNumeroEstudiantes()).isEqualTo(1);
        assertThat(datos).isEqualTo(new DatosOutputRegistrarEstudianteCurso(estudianteTest, cursoTest1));
    }

    @DisplayName("Test - Obtener el listado de todos los cursos")
    @Test
    void obtenerListadoCursos() {
        // given: Configuración de condiciones iniciales y datos de prueba
        // Configuración de la paginación con un tamaño de página de 10 y ordenado por nombre
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        // Creación de una lista de cursos de prueba
        List<Curso> cursos = new ArrayList<>();
        cursos.add(cursoTest1);
        cursos.add(cursoTest2);
        cursos.add(cursoTest3);

        // Simula la obtención de todos los cursos paginados desde el repositorio
        given(cursoRepository.findAll(paginacion)).willReturn(new PageImpl<>(cursos, paginacion, cursos.size()));

        // when: Ejecución del método a probar, que obtiene el listado de cursos
        var response = service.obtenerListadoCursos(paginacion);

        // then: Verificación de los resultados esperados
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(cursos.size());
        assertThat(response).contains(new DatosOutputCurso(cursoTest1));
        assertThat(response).contains(new DatosOutputCurso(cursoTest2));
        assertThat(response).contains(new DatosOutputCurso(cursoTest3));
    }

    @DisplayName("Test - Obtener el listado de todos los cursos registrados con el id de un docente")
    @Test
    void obtenerListadoCursosPorIdDocente() {
        // given: Configuración de condiciones iniciales y datos de prueba

        // Configuración de la paginación con un tamaño de página de 10 y ordenado por nombre
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        // Creación de una lista de cursos de prueba
        List<Curso> cursos = new ArrayList<>();
        cursos.add(cursoTest1);
        cursos.add(cursoTest2);
        cursos.add(cursoTest3);
        cursos.add(cursoTest4);
        cursos.add(cursoTest5);

        // Simulación de la obtención del docente por su ID desde el repositorio de usuarios
        given(usuarioRepository.getReferenceById(docenteTest1.getId())).willReturn(docenteTest1);

        // Simulación de la obtención de cursos por ID del docente desde el repositorio de cursos,
        // filtrando los cursos asociados al docenteTest1
        given(cursoRepository.obtenerCursosPorIdDocente(paginacion, docenteTest1.getId())).willReturn(
                new PageImpl<>(
                        cursos.stream()
                                .filter(c -> c.getDocente() == docenteTest1)
                                .toList(),
                        paginacion, cursos.size())); // Ajuste del tamaño para reflejar el filtro

        // when: Ejecución del método a probar, que obtiene el listado de cursos por ID del docente
        var response = service.obtenerListadoCursosPorIdUsuario(paginacion, docenteTest1.getId());

        // then: Verificación de los resultados esperados
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(cursos.size() - 1);
        assertThat(response).contains(new DatosOutputCurso(cursoTest1));
        assertThat(response).contains(new DatosOutputCurso(cursoTest2));
        assertThat(response).contains(new DatosOutputCurso(cursoTest3));
        assertThat(response).doesNotContain(new DatosOutputCurso(cursoTest4));
        assertThat(response).contains(new DatosOutputCurso(cursoTest5));
    }

    @DisplayName("Test - Obtener el listado de todos los cursos registrados con el id de un estudiante")
    @Test
    void obtenerListadoCursosPorIdEstudiante() {
        // given: Configuración de condiciones iniciales y datos de prueba
        // Configuración de la paginación con un tamaño de página de 10 y ordenado por nombre
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        // Registro del estudianteTest en varios cursos de prueba
        cursoTest1.registrarEstudiante(estudianteTest);
        cursoTest2.registrarEstudiante(estudianteTest);
        cursoTest5.registrarEstudiante(estudianteTest);

        // Creación de una lista de cursos de prueba
        List<Curso> cursos = new ArrayList<>();
        cursos.add(cursoTest1);
        cursos.add(cursoTest2);
        cursos.add(cursoTest3);
        cursos.add(cursoTest4);
        cursos.add(cursoTest5);

        // Simulación de la obtención del estudiante por su ID desde el repositorio de usuarios
        given(usuarioRepository.getReferenceById(estudianteTest.getId())).willReturn(estudianteTest);

        // Simulación de la obtención de cursos por ID del estudiante desde el repositorio de cursos,
        // filtrando los cursos en los que está registrado el estudianteTest
        given(cursoRepository.obtenerCursosPorIdEstudiante(paginacion, estudianteTest.getId())).willReturn(
                new PageImpl<>(
                        cursos.stream()
                                .filter(c -> c.getEstudiantes().stream().anyMatch(e -> e.getId().equals(estudianteTest.getId())))
                                .collect(Collectors.toList()),
                        paginacion, cursos.size()));

        // when: Ejecución del método a probar, que obtiene el listado de cursos por ID del estudiante
        var response = service.obtenerListadoCursosPorIdUsuario(paginacion, estudianteTest.getId());

        // then: Verificación de los resultados esperados
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(cursos.size() - 2);
        assertThat(response).contains(new DatosOutputCurso(cursoTest1));
        assertThat(response).contains(new DatosOutputCurso(cursoTest2));
        assertThat(response).doesNotContain(new DatosOutputCurso(cursoTest3));
        assertThat(response).doesNotContain(new DatosOutputCurso(cursoTest4));
        assertThat(response).contains(new DatosOutputCurso(cursoTest5));
    }
}
