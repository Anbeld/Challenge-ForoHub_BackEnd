package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.curso.Categoria;
import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.respuesta.DatosInputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.DatosOutputRespuesta;
import com.github.anbeld.ForoHub.domain.respuesta.Respuesta;
import com.github.anbeld.ForoHub.domain.respuesta.RespuestaService;
import com.github.anbeld.ForoHub.domain.topico.Topico;
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
class RespuestaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputRespuesta> DatosInputRespuestaJacksonTester;

    @MockBean
    private RespuestaService service;

    // Datos de prueba
    private Usuario docenteTest;
    private Usuario estudianteTest;
    private Curso cursoTest;
    private Topico topicoTest;
    private Respuesta respuestaTest1;
    private Respuesta respuestaTest2;
    private Respuesta respuestaTest3;
    private Respuesta respuestaTest4;
    private Respuesta respuestaTest5;
    private Respuesta respuestaTest6;

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
                .id(2L)
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
                .url("/api/cursos/1")
                .build();

        topicoTest = Topico.builder()
                .id(1L)
                .titulo("Tópico Test")
                .mensaje("Este es el primer tópico")
                .resuelto(false)
                .fechaCreacion(LocalDateTime.now())
                .autor(docenteTest)
                .curso(cursoTest)
                .respuestas(new ArrayList<>())
                .url("/api/cursos/1").build();

        respuestaTest1 = Respuesta.builder()
                .id(1L)
                .topico(topicoTest)
                .fechaCreacion(LocalDateTime.now())
                .autor(estudianteTest)
                .respuesta("Esta es la primera respuesta")
                .url("/api/respuestas/2")
                .build();

        respuestaTest2 = Respuesta.builder().id(2L).topico(topicoTest).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).respuesta("Esta es la segunda respuesta").url("/api/respuestas/2").build();
        respuestaTest3 = Respuesta.builder().id(3L).topico(topicoTest).fechaCreacion(LocalDateTime.now()).autor(docenteTest).respuesta("Esta es la tercera respuesta").url("/api/respuestas/3").build();
        respuestaTest4 = Respuesta.builder().id(4L).topico(topicoTest).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).respuesta("Esta es la cuarta respuesta").url("/api/respuestas/4").build();
        respuestaTest5 = Respuesta.builder().id(5L).topico(topicoTest).fechaCreacion(LocalDateTime.now()).autor(docenteTest).respuesta("Esta es la quinta respuesta").url("/api/respuestas/5").build();
        respuestaTest6 = Respuesta.builder().id(6L).topico(topicoTest).fechaCreacion(LocalDateTime.now()).autor(estudianteTest).respuesta("Esta es la sexta respuesta").url("/api/respuestas/6").build();
    }

    @Test
    @WithMockUser
    void registrarRespuesta() throws Exception {
        // given: Preparación de datos para la prueba
        DatosInputRespuesta datosInput = new DatosInputRespuesta(
                respuestaTest1.getTopico().getId(),
                respuestaTest1.getAutor().getId(),
                respuestaTest1.getRespuesta()
        );

        // Configuración del comportamiento esperado del servicio
        given(service.registrarRespuesta(any(DatosInputRespuesta.class), any(UriComponentsBuilder.class)))
                .willReturn(respuestaTest1);

        // when: Realización de la petición para registrar una respuesta
        ResultActions response = mockMvc.perform(post("/api/respuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DatosInputRespuestaJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.respuesta_id").value(respuestaTest1.getId()))
                .andExpect(jsonPath("$.titulo_topico").value(respuestaTest1.getTopico().getTitulo()))
                .andExpect(jsonPath("$.curso").value(respuestaTest1.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.autor").value(respuestaTest1.getAutor().getUserName()))
                .andExpect(jsonPath("$.respuesta").value(respuestaTest1.getRespuesta()));
    }

    @Test
    @WithMockUser
    void obtenerRespuestasPorTopicosActivos() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Creación de una lista de respuestas para tópicos activos
        List<Respuesta> listaRespuestasTopicosActivos = new ArrayList<>();
        listaRespuestasTopicosActivos.add(respuestaTest1);
        listaRespuestasTopicosActivos.add(respuestaTest2);
        listaRespuestasTopicosActivos.add(respuestaTest3);
        listaRespuestasTopicosActivos.add(respuestaTest4);
        listaRespuestasTopicosActivos.add(respuestaTest5);
        listaRespuestasTopicosActivos.add(respuestaTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerRespuestasPorTopicosActivos(paginacion)).willReturn(new PageImpl<>(listaRespuestasTopicosActivos.stream().filter(r -> !r.getTopico().isResuelto()).map(DatosOutputRespuesta::new).toList(), paginacion, listaRespuestasTopicosActivos.size()));

        // when: Realización de la petición para obtener respuestas por tópicos activos
        ResultActions response = mockMvc.perform(get("/api/respuestas"));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaRespuestasTopicosActivos.size())))
                .andExpect(jsonPath("$.content[0].respuesta_id").value(respuestaTest1.getId()))
                .andExpect(jsonPath("$.content[0].titulo_topico").value(respuestaTest1.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[0].curso").value(respuestaTest1.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[0].autor").value(respuestaTest1.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[0].respuesta").value(respuestaTest1.getRespuesta()))

                .andExpect(jsonPath("$.content[1].respuesta_id").value(respuestaTest2.getId()))
                .andExpect(jsonPath("$.content[1].titulo_topico").value(respuestaTest2.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[1].curso").value(respuestaTest2.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[1].autor").value(respuestaTest2.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[1].respuesta").value(respuestaTest2.getRespuesta()))

                .andExpect(jsonPath("$.content[2].respuesta_id").value(respuestaTest3.getId()))
                .andExpect(jsonPath("$.content[2].titulo_topico").value(respuestaTest3.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[2].curso").value(respuestaTest3.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[2].autor").value(respuestaTest3.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[2].respuesta").value(respuestaTest3.getRespuesta()))

                .andExpect(jsonPath("$.content[3].respuesta_id").value(respuestaTest4.getId()))
                .andExpect(jsonPath("$.content[3].titulo_topico").value(respuestaTest4.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[3].curso").value(respuestaTest4.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[3].autor").value(respuestaTest4.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[3].respuesta").value(respuestaTest4.getRespuesta()))

                .andExpect(jsonPath("$.content[4].respuesta_id").value(respuestaTest5.getId()))
                .andExpect(jsonPath("$.content[4].titulo_topico").value(respuestaTest5.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[4].curso").value(respuestaTest5.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[4].autor").value(respuestaTest5.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[4].respuesta").value(respuestaTest5.getRespuesta()))

                .andExpect(jsonPath("$.content[5].respuesta_id").value(respuestaTest6.getId()))
                .andExpect(jsonPath("$.content[5].titulo_topico").value(respuestaTest6.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[5].curso").value(respuestaTest6.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[5].autor").value(respuestaTest6.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[5].respuesta").value(respuestaTest6.getRespuesta()));
    }

    @Test
    @WithMockUser
    void obtenerRespuestasPorAutorId() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Creación de una lista de respuestas
        List<Respuesta> listaRespuestasTopicosActivos = new ArrayList<>();
        listaRespuestasTopicosActivos.add(respuestaTest1);
        listaRespuestasTopicosActivos.add(respuestaTest2);
        listaRespuestasTopicosActivos.add(respuestaTest3);
        listaRespuestasTopicosActivos.add(respuestaTest4);
        listaRespuestasTopicosActivos.add(respuestaTest5);
        listaRespuestasTopicosActivos.add(respuestaTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerRespuestasPorAutorId(paginacion, estudianteTest.getId())).willReturn(new PageImpl<>(listaRespuestasTopicosActivos.stream().filter(r -> r.getAutor().getId().equals(estudianteTest.getId())).map(DatosOutputRespuesta::new).toList(), paginacion, listaRespuestasTopicosActivos.size()));

        // when: Realización de la petición para obtener respuestas por autor ID
        ResultActions response = mockMvc.perform(get("/api/respuestas/autor/{id}", estudianteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaRespuestasTopicosActivos.size() - 2)))
                .andExpect(jsonPath("$.content[0].respuesta_id").value(respuestaTest1.getId()))
                .andExpect(jsonPath("$.content[0].titulo_topico").value(respuestaTest1.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[0].curso").value(respuestaTest1.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[0].autor").value(respuestaTest1.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[0].respuesta").value(respuestaTest1.getRespuesta()))

                .andExpect(jsonPath("$.content[1].respuesta_id").value(respuestaTest2.getId()))
                .andExpect(jsonPath("$.content[1].titulo_topico").value(respuestaTest2.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[1].curso").value(respuestaTest2.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[1].autor").value(respuestaTest2.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[1].respuesta").value(respuestaTest2.getRespuesta()))

                .andExpect(jsonPath("$.content[2].respuesta_id").value(respuestaTest4.getId()))
                .andExpect(jsonPath("$.content[2].titulo_topico").value(respuestaTest4.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[2].curso").value(respuestaTest4.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[2].autor").value(respuestaTest4.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[2].respuesta").value(respuestaTest4.getRespuesta()))

                .andExpect(jsonPath("$.content[3].respuesta_id").value(respuestaTest6.getId()))
                .andExpect(jsonPath("$.content[3].titulo_topico").value(respuestaTest6.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[3].curso").value(respuestaTest6.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[3].autor").value(respuestaTest6.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[3].respuesta").value(respuestaTest6.getRespuesta()));
    }

    @Test
    @WithMockUser
    void obtenerRespuestasPorTopicoId() throws Exception {
        // given: Preparación de datos de paginación
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("fechaCreacion"));

        // Creación de una lista de respuestas
        List<Respuesta> listaRespuestasTopicosActivos = new ArrayList<>();
        listaRespuestasTopicosActivos.add(respuestaTest1);
        listaRespuestasTopicosActivos.add(respuestaTest2);
        listaRespuestasTopicosActivos.add(respuestaTest3);
        listaRespuestasTopicosActivos.add(respuestaTest4);
        listaRespuestasTopicosActivos.add(respuestaTest5);
        listaRespuestasTopicosActivos.add(respuestaTest6);

        // Configuración del comportamiento esperado del servicio
        given(service.obtenerRespuestasPorAutorId(paginacion, estudianteTest.getId())).willReturn(new PageImpl<>(listaRespuestasTopicosActivos.stream().filter(r -> r.getTopico().getId().equals(topicoTest.getId())).map(DatosOutputRespuesta::new).toList(), paginacion, listaRespuestasTopicosActivos.size()));

        // when: Realización de la petición para obtener respuestas por tópico ID
        ResultActions response = mockMvc.perform(get("/api/respuestas/autor/{id}", estudianteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.totalElements", is(listaRespuestasTopicosActivos.size())))
                .andExpect(jsonPath("$.content[0].respuesta_id").value(respuestaTest1.getId()))
                .andExpect(jsonPath("$.content[0].titulo_topico").value(respuestaTest1.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[0].curso").value(respuestaTest1.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[0].autor").value(respuestaTest1.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[0].respuesta").value(respuestaTest1.getRespuesta()))
                .andExpect(jsonPath("$.content[1].respuesta_id").value(respuestaTest2.getId()))
                .andExpect(jsonPath("$.content[1].titulo_topico").value(respuestaTest2.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[1].curso").value(respuestaTest2.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[1].autor").value(respuestaTest2.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[1].respuesta").value(respuestaTest2.getRespuesta()))
                .andExpect(jsonPath("$.content[2].respuesta_id").value(respuestaTest3.getId()))
                .andExpect(jsonPath("$.content[2].titulo_topico").value(respuestaTest3.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[2].curso").value(respuestaTest3.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[2].autor").value(respuestaTest3.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[2].respuesta").value(respuestaTest3.getRespuesta()))
                .andExpect(jsonPath("$.content[3].respuesta_id").value(respuestaTest4.getId()))
                .andExpect(jsonPath("$.content[3].titulo_topico").value(respuestaTest4.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[3].curso").value(respuestaTest4.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[3].autor").value(respuestaTest4.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[3].respuesta").value(respuestaTest4.getRespuesta()))
                .andExpect(jsonPath("$.content[4].respuesta_id").value(respuestaTest5.getId()))
                .andExpect(jsonPath("$.content[4].titulo_topico").value(respuestaTest5.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[4].curso").value(respuestaTest5.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[4].autor").value(respuestaTest5.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[4].respuesta").value(respuestaTest5.getRespuesta()))
                .andExpect(jsonPath("$.content[5].respuesta_id").value(respuestaTest6.getId()))
                .andExpect(jsonPath("$.content[5].titulo_topico").value(respuestaTest6.getTopico().getTitulo()))
                .andExpect(jsonPath("$.content[5].curso").value(respuestaTest6.getTopico().getCurso().getNombre()))
                .andExpect(jsonPath("$.content[5].autor").value(respuestaTest6.getAutor().getUserName()))
                .andExpect(jsonPath("$.content[5].respuesta").value(respuestaTest6.getRespuesta()));
    }
}