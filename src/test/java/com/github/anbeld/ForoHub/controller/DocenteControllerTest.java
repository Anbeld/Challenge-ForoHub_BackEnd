package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.*;
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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
class DocenteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputRegistrarUsuario> datosInputRegistrarUsuarioJacksonTester;

    @Autowired
    private JacksonTester<DatosInputActualizarPasswordUsuario> datosInputActualizarPasswordUsuarioJacksonTester;

    @MockBean
    private UsuarioService service;

    // Datos de prueba
    private Usuario docenteTest;

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
                .url("/api/docentes/1")
                .build();
    }

    @Test
    @WithMockUser
    void registrarDocente() throws Exception {
        // given: Preparación de datos
        DatosInputRegistrarUsuario datosInput = new DatosInputRegistrarUsuario(
                "Maria Docente",
                "maria.docente@foro.com",
                "password"
        );

        given(service.registrarUsuario(any(DatosInputRegistrarUsuario.class), eq(Perfil.DOCENTE), any(UriComponentsBuilder.class)))
                .willReturn(docenteTest);

        // when: Realización de la petición para registrar un docente
        ResultActions response = mockMvc.perform(post("/api/docentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputRegistrarUsuarioJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario_id").value(docenteTest.getId()))
                .andExpect(jsonPath("$.nombre").value(docenteTest.getUserName()))
                .andExpect(jsonPath("$.email").value(docenteTest.getEmail()))
                .andExpect(jsonPath("$.perfil").value(docenteTest.getUserRole().toString()));
    }

    @Test
    @WithMockUser
    void listadoDocentesRegistrados() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("userName"));

        // Creación de lista de docentes simulados para la prueba
        List<DatosOutputUsuario> listaEstudiantes = new ArrayList<>();
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(2L).userName("Gabriel Ramirez").email("gabriel.ramirez@foro..com").password("password").status(true).userRole(Perfil.DOCENTE).url("estudiantes/2").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(3L).userName("Julen Ramirez").email("julen.ramirez@foro..com").password("password").status(true).userRole(Perfil.DOCENTE).url("estudiantes/3").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(4L).userName("Biaggio Ramirez").email("biaggio.ramirez@foro..com").password("password").status(true).userRole(Perfil.DOCENTE).url("estudiantes/4").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(5L).userName("Adrian Ramirez").email("adrian.ramirez@foro..com").password("password").status(true).userRole(Perfil.DOCENTE).url("estudiantes/5").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(6L).userName("Christian Ramirez").email("christian.ramirez@foro..com").password("password").status(true).userRole(Perfil.DOCENTE).url("estudiantes/6").cursosEstudiante(new ArrayList<>()).build()));

        given(service.listadoUsuariosActivosPorPerfil(paginacion, docenteTest.getUserRole())).willReturn(new PageImpl<>(listaEstudiantes, paginacion, listaEstudiantes.size()));

        // when: Realización de la petición para obtener el listado de docentes
        ResultActions response = mockMvc.perform(get("/api/docentes"));

        //then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(listaEstudiantes.size())))
                .andExpect(jsonPath("$.content[*].usuario_id", hasItem(2)))
                .andExpect(jsonPath("$.content[*].nombre", hasItems("Gabriel Ramirez", "Julen Ramirez", "Biaggio Ramirez", "Adrian Ramirez", "Christian Ramirez")));

    }

    @Test
    @WithMockUser
    void obtenerDocentePorId() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        given(service.obtenerDocentePorId(docenteTest.getId()))
                .willReturn(new DatosOutputDocentePorId(docenteTest));

        //when: Realización de la petición para obtener un docente por ID
        ResultActions response = mockMvc.perform(get("/api/docentes/{id}", docenteTest.getId()));

        //then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.usuario_id").value(docenteTest.getId()))
                .andExpect(jsonPath("$.nombre").value(docenteTest.getUserName()))
                .andExpect(jsonPath("$.email").value(docenteTest.getEmail()))
                .andExpect(jsonPath("$.perfil").value(docenteTest.getUserRole().toString()));
    }

    @Test
    @WithMockUser
    void actualizarPasswordDocente() throws Exception {
        // given: Preparación de datos
        String newPassword = "newPassword";
        DatosInputActualizarPasswordUsuario datos = new DatosInputActualizarPasswordUsuario(docenteTest.getId(), docenteTest.getEmail(), docenteTest.getPassword(), newPassword);

        Usuario docenteNewPassword = docenteTest;

        // Configuración del comportamiento esperado del servicio
        given(service.actualizarPasswordUsuario(datos, docenteTest.getUserRole())).willReturn(new DatosOutputUsuario(docenteNewPassword));

        // when: Realización de la petición para actualizar la contraseña de un docente
        docenteNewPassword.actualizarPassword(newPassword);
        ResultActions response = mockMvc.perform(put("/api/docentes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputActualizarPasswordUsuarioJacksonTester.write(datos).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre", is(docenteTest.getUserName())))
                .andExpect(jsonPath("$.email", is(docenteTest.getEmail())))
                .andExpect(jsonPath("$.perfil", is(docenteTest.getUserRole().toString())))
                .andExpect(jsonPath("$.usuario_id", is(docenteTest.getId().intValue())));
        ;
    }

    @Test
    @WithMockUser
    void deleteLogicoDocente() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        willDoNothing().given(service).desactivarUsuario(docenteTest.getId(), docenteTest.getUserRole());

        // when: Realización de la petición para desactivar un docente
        ResultActions response = mockMvc.perform(delete("/api/docentes/{id}", docenteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}