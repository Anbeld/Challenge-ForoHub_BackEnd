package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.topico.DatosInputTopico;
import com.github.anbeld.ForoHub.domain.topico.DatosOutputTopico;
import com.github.anbeld.ForoHub.domain.topico.Topico;
import com.github.anbeld.ForoHub.domain.topico.TopicoService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@SuppressWarnings("all")
class TopicoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputTopico> DatosInputTopicoJacksonTester;

    @MockBean
    private TopicoService service;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Curso cursoTest;
    private Topico topicoTest1;
    private Topico topicoTest2;
    private Topico topicoTest3;
    private Topico topicoTest4;
    private Topico topicoTest5;
    private Topico topicoTest6;

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

        cursoTest = Curso.builder()
                .id(1L)
                .nombre("Curso Test")
                .categoria(Categoria.DEVOPS)
                .numeroEstudiantes(0)
                .docente(docenteTest)
                .estudiantes(new ArrayList<>())
                .url("cursos/1")
                .build();

        topicoTest1 = Topico.builder()
                .id(1L)
                .titulo("Tópico Test")
                .mensaje("Este es el primer tópico")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .curso(cursoTest)
                .url("/api/topicos/1").build();

        topicoTest2 = Topico.builder().id(2L).titulo("Tópico Test").mensaje("Este es el segundo tópico").resuelto(false).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).curso(cursoTest).url("/api/topicos/2").build();
        topicoTest3 = Topico.builder().id(3L).titulo("Tópico Test").mensaje("Este es el tercer tópico").resuelto(false).fechaCreacion(LocalDateTime.now()).autor(docenteTest).curso(cursoTest).url("/api/topicos/3").build();
        topicoTest4 = Topico.builder().id(4L).titulo("Tópico Test").mensaje("Este es el cuarto tópico").resuelto(false).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).curso(cursoTest).url("/api/topicos/4").build();
        topicoTest5 = Topico.builder().id(5L).titulo("Tópico Test").mensaje("Este es el quinto tópico").resuelto(false).fechaCreacion(LocalDateTime.now()).autor(docenteTest).curso(cursoTest).url("/api/topicos/5").build();
        topicoTest6 = Topico.builder().id(6L).titulo("Tópico Test").mensaje("Este es el sexto tópico").resuelto(false).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).curso(cursoTest).url("/api/topicos/6").build();
    }

    @Test
    @WithMockUser
    void registrarTopico() throws Exception {
        // given: Preparación de datos para la prueba        
        DatosInputTopico datosInput = new DatosInputTopico(
                topicoTest1.getCurso().getId(),
                topicoTest1.getAutor().getId(),
                topicoTest1.getTitulo(),
                topicoTest1.getMensaje()
        );

        // Configuración del comportamiento esperado del servicio
        given(service.registrarTopico(any(DatosInputTopico.class), any(UriComponentsBuilder.class)))
                .willReturn(topicoTest1);

        // when: Realización de la petición para registrar un tópico
        ResultActions response = mockMvc.perform(post("/api/topicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DatosInputTopicoJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topico_id").value(topicoTest1.getId()))
                .andExpect(jsonPath("$.autor").value(topicoTest1.getAutor().getUserName()))
                .andExpect(jsonPath("$.titulo").value(topicoTest1.getTitulo()))
                .andExpect(jsonPath("$.mensaje").value(topicoTest1.getMensaje()))
                .andExpect(jsonPath("$.resuelto").value(topicoTest1.isResuelto()))
                .andExpect(jsonPath("$.nombre_curso").value(topicoTest1.getCurso().getNombre()));
    }

    @Test
    @WithMockUser
    void obtenerTopicosActivos() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        topicoTest1.cerrarTopico();
        topicoTest3.cerrarTopico();
        topicoTest5.cerrarTopico();

        // Creación de una lista de topicos
        List<Topico> listaTopicos = new ArrayList<>();
        listaTopicos.add(topicoTest1);
        listaTopicos.add(topicoTest2);
        listaTopicos.add(topicoTest3);
        listaTopicos.add(topicoTest4);
        listaTopicos.add(topicoTest5);
        listaTopicos.add(topicoTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerTopicos(paginacion)).willReturn(new PageImpl<>(listaTopicos.stream().filter(t -> !t.isResuelto()).map(DatosOutputTopico::new).toList(), paginacion, listaTopicos.size()));

        // when: Realización de la petición para obtener todos los tópicos activos
        ResultActions response = mockMvc.perform(get("/api/topicos/all"));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaTopicos.size() - 3)))
                .andExpect(jsonPath("$.content[0].topico_id").value(topicoTest2.getId()))
                .andExpect(jsonPath("$.content[0].titulo").value(topicoTest2.getTitulo()))
                .andExpect(jsonPath("$.content[0].mensaje").value(topicoTest2.getMensaje()))
                .andExpect(jsonPath("$.content[1].topico_id").value(topicoTest4.getId()))
                .andExpect(jsonPath("$.content[1].titulo").value(topicoTest4.getTitulo()))
                .andExpect(jsonPath("$.content[1].mensaje").value(topicoTest4.getMensaje()))
                .andExpect(jsonPath("$.content[2].topico_id").value(topicoTest6.getId()))
                .andExpect(jsonPath("$.content[2].titulo").value(topicoTest6.getTitulo()))
                .andExpect(jsonPath("$.content[2].mensaje").value(topicoTest6.getMensaje()));
    }

    @Test
    @WithMockUser
    void obtenerTopicosPorStatusFalse() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        topicoTest1.cerrarTopico();
        topicoTest3.cerrarTopico();
        topicoTest5.cerrarTopico();

        // Creación de una lista de topicos
        List<Topico> listaTopicos = new ArrayList<>();
        listaTopicos.add(topicoTest1);
        listaTopicos.add(topicoTest2);
        listaTopicos.add(topicoTest3);
        listaTopicos.add(topicoTest4);
        listaTopicos.add(topicoTest5);
        listaTopicos.add(topicoTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerTopicosPorStatus(paginacion, false)).willReturn(new PageImpl<>(listaTopicos.stream().filter(t -> !t.isResuelto()).map(DatosOutputTopico::new).toList(), paginacion, listaTopicos.size()));

        // when: Realización de la petición para obtener todos los tópicos activos
        ResultActions response = mockMvc.perform(get("/api/topicos/{status}", false));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaTopicos.size() - 3)))
                .andExpect(jsonPath("$.content[0].topico_id").value(topicoTest2.getId()))
                .andExpect(jsonPath("$.content[0].titulo").value(topicoTest2.getTitulo()))
                .andExpect(jsonPath("$.content[0].mensaje").value(topicoTest2.getMensaje()))
                .andExpect(jsonPath("$.content[1].topico_id").value(topicoTest4.getId()))
                .andExpect(jsonPath("$.content[1].titulo").value(topicoTest4.getTitulo()))
                .andExpect(jsonPath("$.content[1].mensaje").value(topicoTest4.getMensaje()))
                .andExpect(jsonPath("$.content[2].topico_id").value(topicoTest6.getId()))
                .andExpect(jsonPath("$.content[2].titulo").value(topicoTest6.getTitulo()))
                .andExpect(jsonPath("$.content[2].mensaje").value(topicoTest6.getMensaje()));
    }

    @Test
    @WithMockUser
    void obtenerTopicosPorStatuTrue() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        topicoTest1.cerrarTopico();
        topicoTest3.cerrarTopico();
        topicoTest5.cerrarTopico();

        // Creación de una lista de topicos
        List<Topico> listaTopicos = new ArrayList<>();
        listaTopicos.add(topicoTest1);
        listaTopicos.add(topicoTest2);
        listaTopicos.add(topicoTest3);
        listaTopicos.add(topicoTest4);
        listaTopicos.add(topicoTest5);
        listaTopicos.add(topicoTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerTopicosPorStatus(paginacion, true)).willReturn(new PageImpl<>(listaTopicos.stream().filter(t -> t.isResuelto()).map(DatosOutputTopico::new).toList(), paginacion, listaTopicos.size()));

        // when: Realización de la petición para obtener todos los tópicos cerrados
        ResultActions response = mockMvc.perform(get("/api/topicos/{status}", true));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaTopicos.size() - 3)))
                .andExpect(jsonPath("$.content[0].topico_id").value(topicoTest1.getId()))
                .andExpect(jsonPath("$.content[0].titulo").value(topicoTest1.getTitulo()))
                .andExpect(jsonPath("$.content[0].mensaje").value(topicoTest1.getMensaje()))
                .andExpect(jsonPath("$.content[1].topico_id").value(topicoTest3.getId()))
                .andExpect(jsonPath("$.content[1].titulo").value(topicoTest3.getTitulo()))
                .andExpect(jsonPath("$.content[1].mensaje").value(topicoTest3.getMensaje()))
                .andExpect(jsonPath("$.content[2].topico_id").value(topicoTest5.getId()))
                .andExpect(jsonPath("$.content[2].titulo").value(topicoTest5.getTitulo()))
                .andExpect(jsonPath("$.content[2].mensaje").value(topicoTest5.getMensaje()));
    }

    @Test
    @WithMockUser
    void obtenerTopicos() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        topicoTest1.cerrarTopico();
        topicoTest3.cerrarTopico();
        topicoTest5.cerrarTopico();

        // Creación de una lista de todos los tópicos
        List<Topico> listaTopicos = new ArrayList<>();
        listaTopicos.add(topicoTest1);
        listaTopicos.add(topicoTest2);
        listaTopicos.add(topicoTest3);
        listaTopicos.add(topicoTest4);
        listaTopicos.add(topicoTest5);
        listaTopicos.add(topicoTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerTopicosPorStatus(paginacion, true)).willReturn(new PageImpl<>(listaTopicos.stream().map(DatosOutputTopico::new).toList(), paginacion, listaTopicos.size()));

        // when: Realización de la petición para obtener todos los tópicos
        ResultActions response = mockMvc.perform(get("/api/topicos/{status}", true));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaTopicos.size())))

                .andExpect(jsonPath("$.content[0].topico_id").value(topicoTest1.getId()))
                .andExpect(jsonPath("$.content[0].titulo").value(topicoTest1.getTitulo()))
                .andExpect(jsonPath("$.content[0].mensaje").value(topicoTest1.getMensaje()))
                .andExpect(jsonPath("$.content[1].topico_id").value(topicoTest2.getId()))
                .andExpect(jsonPath("$.content[1].titulo").value(topicoTest2.getTitulo()))
                .andExpect(jsonPath("$.content[1].mensaje").value(topicoTest2.getMensaje()))
                .andExpect(jsonPath("$.content[2].topico_id").value(topicoTest3.getId()))
                .andExpect(jsonPath("$.content[2].titulo").value(topicoTest3.getTitulo()))
                .andExpect(jsonPath("$.content[2].mensaje").value(topicoTest3.getMensaje()))
                .andExpect(jsonPath("$.content[3].topico_id").value(topicoTest4.getId()))
                .andExpect(jsonPath("$.content[3].titulo").value(topicoTest4.getTitulo()))
                .andExpect(jsonPath("$.content[3].mensaje").value(topicoTest4.getMensaje()))
                .andExpect(jsonPath("$.content[4].topico_id").value(topicoTest5.getId()))
                .andExpect(jsonPath("$.content[4].titulo").value(topicoTest5.getTitulo()))
                .andExpect(jsonPath("$.content[4].mensaje").value(topicoTest5.getMensaje()))
                .andExpect(jsonPath("$.content[5].topico_id").value(topicoTest6.getId()))
                .andExpect(jsonPath("$.content[5].titulo").value(topicoTest6.getTitulo()))
                .andExpect(jsonPath("$.content[5].mensaje").value(topicoTest6.getMensaje()))
        ;
    }

    @Test
    @WithMockUser
    void cerrarTopico() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        willDoNothing().given(service).cerrarTopico(topicoTest1.getId());

        // when: Realización de la petición para cerrar un tópico
        ResultActions response = mockMvc.perform(delete("/api/topicos/{id}", docenteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}