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
class EstudianteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputRegistrarUsuario> datosInputRegistrarUsuarioJacksonTester;

    @Autowired
    private JacksonTester<DatosInputActualizarPasswordUsuario> datosInputActualizarPasswordUsuarioJacksonTester;

    @MockBean
    private UsuarioService service;

    // Datos de prueba
    private Usuario estudianteTest;

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
                .url("/api/estudiantes/1")
                .build();
    }

    @Test
    @WithMockUser
    void registrarEstudiante() throws Exception {
        // given: Preparación de datos
        DatosInputRegistrarUsuario datosInput = new DatosInputRegistrarUsuario(
                "Pablo Estudiante",
                "pablo.estudiante@foro.com",
                "password"
        );

        given(service.registrarUsuario(any(DatosInputRegistrarUsuario.class), eq(Perfil.ESTUDIANTE), any(UriComponentsBuilder.class)))
                .willReturn(estudianteTest);

        // when: Realización de la petición para registrar un estudiante
        ResultActions response = mockMvc.perform(post("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputRegistrarUsuarioJacksonTester.write(datosInput).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario_id").value(estudianteTest.getId()))
                .andExpect(jsonPath("$.nombre").value(estudianteTest.getUserName()))
                .andExpect(jsonPath("$.email").value(estudianteTest.getEmail()))
                .andExpect(jsonPath("$.perfil").value(estudianteTest.getUserRole().toString()));
    }

    @Test
    @WithMockUser
    void listadoEstudiantesRegistrados() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("userName"));

        // Creación de lista de estudiantes simulados para la prueba
        List<DatosOutputUsuario> listaEstudiantes = new ArrayList<>();
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(2L).userName("Gabriel Ramirez").email("gabriel.ramirez@foro..com").password("password").status(true).userRole(Perfil.ESTUDIANTE).url("estudiantes/2").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(3L).userName("Julen Ramirez").email("julen.ramirez@foro..com").password("password").status(true).userRole(Perfil.ESTUDIANTE).url("estudiantes/3").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(4L).userName("Biaggio Ramirez").email("biaggio.ramirez@foro..com").password("password").status(true).userRole(Perfil.ESTUDIANTE).url("estudiantes/4").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(5L).userName("Adrian Ramirez").email("adrian.ramirez@foro..com").password("password").status(true).userRole(Perfil.ESTUDIANTE).url("estudiantes/5").cursosEstudiante(new ArrayList<>()).build()));
        listaEstudiantes.add(new DatosOutputUsuario(Usuario.builder().id(6L).userName("Christian Ramirez").email("christian.ramirez@foro..com").password("password").status(true).userRole(Perfil.ESTUDIANTE).url("estudiantes/6").cursosEstudiante(new ArrayList<>()).build()));

        given(service.listadoUsuariosActivosPorPerfil(paginacion, estudianteTest.getUserRole())).willReturn(new PageImpl<>(listaEstudiantes, paginacion, listaEstudiantes.size()));

        // when: Realización de la petición para obtener el listado de estudiantes
        ResultActions response = mockMvc.perform(get("/api/estudiantes"));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(listaEstudiantes.size())))
                .andExpect(jsonPath("$.content[*].usuario_id", hasItem(2)))
                .andExpect(jsonPath("$.content[*].nombre", hasItems("Gabriel Ramirez", "Julen Ramirez", "Biaggio Ramirez", "Adrian Ramirez", "Christian Ramirez")));
    }

    @Test
    @WithMockUser
    void obtenerEstudiantePorId() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        given(service.obtenerEstudiantePorId(estudianteTest.getId()))
                .willReturn(new DatosOutputEstudiantePorId(estudianteTest));

        //when: Realización de la petición para obtener un estudiante por ID
        ResultActions response = mockMvc.perform(get("/api/estudiantes/{id}", estudianteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.usuario_id").value(estudianteTest.getId()))
                .andExpect(jsonPath("$.nombre").value(estudianteTest.getUserName()))
                .andExpect(jsonPath("$.email").value(estudianteTest.getEmail()))
                .andExpect(jsonPath("$.perfil").value(estudianteTest.getUserRole().toString()));
    }

    @Test
    @WithMockUser
    void actualizarPasswordEstudiante() throws Exception {
        // given: Preparación de datos
        String newPassword = "newPassword";
        DatosInputActualizarPasswordUsuario datos = new DatosInputActualizarPasswordUsuario(estudianteTest.getId(), estudianteTest.getEmail(), estudianteTest.getPassword(), newPassword);

        Usuario estudianteNewPassword = estudianteTest;

        // Configuración del comportamiento esperado del servicio
        given(service.actualizarPasswordUsuario(datos, estudianteTest.getUserRole())).willReturn(new DatosOutputUsuario(estudianteNewPassword));

        // when: Realización de la petición para actualizar la contraseña de un estudiante
        estudianteNewPassword.actualizarPassword(newPassword);
        ResultActions response = mockMvc.perform(put("/api/estudiantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputActualizarPasswordUsuarioJacksonTester.write(datos).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.nombre", is(estudianteTest.getUserName())))
                .andExpect(jsonPath("$.email", is(estudianteTest.getEmail())))
                .andExpect(jsonPath("$.perfil", is(estudianteTest.getUserRole().toString())))
                .andExpect(jsonPath("$.usuario_id", is(estudianteTest.getId().intValue())));
        ;
    }

    @Test
    @WithMockUser
    void deleteLogicoEstudiante() throws Exception {
        // given: Configuración del comportamiento esperado del servicio
        willDoNothing().given(service).desactivarUsuario(estudianteTest.getId(), estudianteTest.getUserRole());

        // when: Realización de la petición para desactivar un estudiante
        ResultActions response = mockMvc.perform(delete("/api/estudiantes/{id}", estudianteTest.getId()));

        // then: Verificación de la respuesta
        response.andExpect(status().isNoContent())
                .andDo(print());
    }
}