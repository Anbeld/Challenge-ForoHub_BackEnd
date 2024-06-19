package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.curso.*;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@SuppressWarnings("all")
class CursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputRegistrarCurso> datosInputRegistrarCursoJacksonTester;

    @Autowired
    private JacksonTester<DatosInputRegistrarEstudianteCurso> datosInputRegistrarEstudianteCursoJacksonTester;

    @MockBean
    private CursoService service;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Curso cursoTest1;
    private Curso cursoTest2;
    private Curso cursoTest3;
    private Curso cursoTest4;
    private Curso cursoTest5;
    private Curso cursoTest6;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        docenteTest = Usuario.builder()
                .id(1L)
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .cursosDocente(new ArrayList<>())
                .build();

        estudianteTest = Usuario.builder()
                .id(1L)
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .cursosEstudiante(new ArrayList<>())
                .build();

        cursoTest1 = Curso.builder()
                .id(1L)
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .url("cursos/1")
                .build();

        cursoTest2 = Curso.builder().id(2L).nombre("Curso Test2").categoria(Categoria.BACKEND).numeroEstudiantes(0).docente(docenteTest).estudiantes(new ArrayList<>()).url("/api/cursos/2").build();
        cursoTest3 = Curso.builder().id(3L).nombre("Curso Test3").categoria(Categoria.FRONTEND).numeroEstudiantes(0).docente(docenteTest).estudiantes(new ArrayList<>()).url("/api/cursos/3").build();
        cursoTest4 = Curso.builder().id(4L).nombre("Curso Test4").categoria(Categoria.DEVOPS).numeroEstudiantes(0).docente(docenteTest).estudiantes(new ArrayList<>()).url("/api/cursos/4").build();
        cursoTest5 = Curso.builder().id(5L).nombre("Curso Test5").categoria(Categoria.FRONTEND).numeroEstudiantes(0).docente(docenteTest).estudiantes(new ArrayList<>()).url("/api/cursos/5").build();
        cursoTest6 = Curso.builder().id(6L).nombre("Curso Test6").categoria(Categoria.BACKEND).numeroEstudiantes(0).docente(docenteTest).estudiantes(new ArrayList<>()).url("/api/cursos/6").build();
    }

    @Test
    @WithMockUser
    void registrarCurso() throws Exception {
        // given: Preparación de datos
        DatosInputRegistrarCurso datosInput = new DatosInputRegistrarCurso(
                "Curso Prueba",
                Categoria.DEVOPS,
                1L
        );

        // Configuración del comportamiento esperado del servicio
        given(service.registrarCurso(any(DatosInputRegistrarCurso.class), any(UriComponentsBuilder.class)))
                .willReturn(cursoTest1);

        // when: Realización de la petición para registrar un curso
        ResultActions response = mockMvc.perform(post("/api/cursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputRegistrarCursoJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.curso_id").value(cursoTest1.getId()))
                .andExpect(jsonPath("$.nombre").value(cursoTest1.getNombre()))
                .andExpect(jsonPath("$.categoria").value(cursoTest1.getCategoria().toString()))
                .andExpect(jsonPath("$.docente").value(cursoTest1.getDocente().getUserName()))
                .andExpect(jsonPath("$.numero_estudiantes").value(cursoTest1.getNumeroEstudiantes()));
    }

    @Test
    @WithMockUser
    void registrarEstudianteEnCurso() throws Exception {
        // given: Preparación de datos
        DatosInputRegistrarEstudianteCurso datosInput = new DatosInputRegistrarEstudianteCurso(cursoTest1.getId(), estudianteTest.getId());
        DatosOutputRegistrarEstudianteCurso datosOutput = new DatosOutputRegistrarEstudianteCurso(estudianteTest, cursoTest1);

        // Configuración del comportamiento esperado del servicio
        given(service.registrarEstudiante(any(DatosInputRegistrarEstudianteCurso.class))).willReturn(datosOutput);

        // when: Realización de la petición para registrar un estudiante en un curso
        ResultActions response = mockMvc.perform(post("/api/cursos/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputRegistrarEstudianteCursoJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre_estudiante").value(estudianteTest.getEmail()))
                .andExpect(jsonPath("$.curso_registrado").value(cursoTest1.getNombre()));
    }

    @Test
    @WithMockUser
    void obtenerListadoCursos() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        List<DatosOutputCurso> listaCursos = new ArrayList<>();
        listaCursos.add(new DatosOutputCurso(cursoTest1));
        listaCursos.add(new DatosOutputCurso(cursoTest2));
        listaCursos.add(new DatosOutputCurso(cursoTest3));
        listaCursos.add(new DatosOutputCurso(cursoTest4));
        listaCursos.add(new DatosOutputCurso(cursoTest5));
        listaCursos.add(new DatosOutputCurso(cursoTest6));

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerListadoCursos(paginacion)).willReturn(new PageImpl<>(listaCursos, paginacion, listaCursos.size()));

        // when: Realización de la petición para obtener el listado de cursos
        ResultActions response = mockMvc.perform(get("/api/cursos"));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaCursos.size())))
                .andExpect(jsonPath("$.content[0].curso_id").value(cursoTest1.getId()))
                .andExpect(jsonPath("$.content[0].nombre").value(cursoTest1.getNombre()))
                .andExpect(jsonPath("$.content[1].curso_id").value(cursoTest2.getId()))
                .andExpect(jsonPath("$.content[1].nombre").value(cursoTest2.getNombre()))
                .andExpect(jsonPath("$.content[2].curso_id").value(cursoTest3.getId()))
                .andExpect(jsonPath("$.content[2].nombre").value(cursoTest3.getNombre()))
                .andExpect(jsonPath("$.content[3].curso_id").value(cursoTest4.getId()))
                .andExpect(jsonPath("$.content[3].nombre").value(cursoTest4.getNombre()))
                .andExpect(jsonPath("$.content[4].curso_id").value(cursoTest5.getId()))
                .andExpect(jsonPath("$.content[4].nombre").value(cursoTest5.getNombre()))
                .andExpect(jsonPath("$.content[5].curso_id").value(cursoTest6.getId()))
                .andExpect(jsonPath("$.content[5].nombre").value(cursoTest6.getNombre()));
    }

    @Test
    @WithMockUser
    void obtenerListadoCursosPorIdDocente() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        List<DatosOutputCurso> listaCursos = new ArrayList<>();
        listaCursos.add(new DatosOutputCurso(cursoTest1));
        listaCursos.add(new DatosOutputCurso(cursoTest2));
        listaCursos.add(new DatosOutputCurso(cursoTest3));
        listaCursos.add(new DatosOutputCurso(cursoTest4));
        listaCursos.add(new DatosOutputCurso(cursoTest5));
        listaCursos.add(new DatosOutputCurso(cursoTest6));

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerListadoCursosPorIdUsuario(paginacion, docenteTest.getId())).willReturn(new PageImpl<>(listaCursos.stream().filter(c -> c.docente() == docenteTest.getUserName()).toList(), paginacion, listaCursos.size()));

        // when: Realización de la petición para obtener el listado de cursos por ID de docente
        ResultActions response = mockMvc.perform(get("/api/cursos/{id}", docenteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaCursos.size())))
                .andExpect(jsonPath("$.content[0].curso_id").value(cursoTest1.getId()))
                .andExpect(jsonPath("$.content[0].nombre").value(cursoTest1.getNombre()))
                .andExpect(jsonPath("$.content[1].curso_id").value(cursoTest2.getId()))
                .andExpect(jsonPath("$.content[1].nombre").value(cursoTest2.getNombre()))
                .andExpect(jsonPath("$.content[2].curso_id").value(cursoTest3.getId()))
                .andExpect(jsonPath("$.content[2].nombre").value(cursoTest3.getNombre()))
                .andExpect(jsonPath("$.content[3].curso_id").value(cursoTest4.getId()))
                .andExpect(jsonPath("$.content[3].nombre").value(cursoTest4.getNombre()))
                .andExpect(jsonPath("$.content[4].curso_id").value(cursoTest5.getId()))
                .andExpect(jsonPath("$.content[4].nombre").value(cursoTest5.getNombre()))
                .andExpect(jsonPath("$.content[5].curso_id").value(cursoTest6.getId()))
                .andExpect(jsonPath("$.content[5].nombre").value(cursoTest6.getNombre()));
    }

    @Test
    @WithMockUser
    void obtenerListadoCursosPorIdEstudiante() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("nombre"));

        cursoTest1.registrarEstudiante(estudianteTest);
        cursoTest4.registrarEstudiante(estudianteTest);
        cursoTest6.registrarEstudiante(estudianteTest);

        List<Curso> listaCursos = new ArrayList<>();
        listaCursos.add(cursoTest1);
        listaCursos.add(cursoTest2);
        listaCursos.add(cursoTest3);
        listaCursos.add(cursoTest4);
        listaCursos.add(cursoTest5);
        listaCursos.add(cursoTest6);

        given(service.obtenerListadoCursosPorIdUsuario(paginacion, docenteTest.getId())).willReturn(new PageImpl<>(listaCursos.stream().filter(c -> c.getEstudiantes().contains(estudianteTest)).map(DatosOutputCurso::new).toList(), paginacion, listaCursos.size()));

        // when: Realización de la petición para obtener el listado de cursos por ID de estudiante
        ResultActions response = mockMvc.perform(get("/api/cursos/{id}", docenteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaCursos.size() - 3)))
                .andExpect(jsonPath("$.content[0].curso_id").value(cursoTest1.getId()))
                .andExpect(jsonPath("$.content[0].nombre").value(cursoTest1.getNombre()))
                .andExpect(jsonPath("$.content[1].curso_id").value(cursoTest4.getId()))
                .andExpect(jsonPath("$.content[1].nombre").value(cursoTest4.getNombre()))
                .andExpect(jsonPath("$.content[2].curso_id").value(cursoTest6.getId()))
                .andExpect(jsonPath("$.content[2].nombre").value(cursoTest6.getNombre()));
    }
}