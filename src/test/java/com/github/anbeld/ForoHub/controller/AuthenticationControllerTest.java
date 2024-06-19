package com.github.anbeld.ForoHub.controller;

import com.github.anbeld.ForoHub.domain.usuario.DatosInputLoginUsuario;
import com.github.anbeld.ForoHub.domain.usuario.Perfil;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import com.github.anbeld.ForoHub.domain.usuario.UsuarioService;
import com.github.anbeld.ForoHub.infra.security.DatosJWTToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@SuppressWarnings("all")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DatosInputLoginUsuario> datosInputLoginUsuarioJacksonTester;

    @MockBean
    private UsuarioService service;
    @Test
    void loginDocente() throws Exception {
        // given: Preparación de datos
        Usuario docenteTest = Usuario.builder()
                .id(1L)
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .url("/docentes/1")
                .build();
        var datosLogin = new DatosInputLoginUsuario(docenteTest.getEmail(), docenteTest.getPassword());

        // Configuración del comportamiento esperado del servicio
        given(service.login(datosLogin)).willReturn(new DatosJWTToken("thisIsTheToken15975345682"));

        // when: Realización de la petición de login
        ResultActions response = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputLoginUsuarioJacksonTester.write(datosLogin).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.jwTtoken", is("thisIsTheToken15975345682")));
    }

    @Test
    void loginEstudiante() throws Exception {
        // given: Preparación de datos
        Usuario estudianteTest = Usuario.builder()
                .id(1L)
                .userName("Juan Estudiante")
                .email("juan.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .url("/estudiantes/1")
                .build();
        var datosLogin = new DatosInputLoginUsuario(estudianteTest.getEmail(), estudianteTest.getPassword());

        // Configuración del comportamiento esperado del servicio
        given(service.login(datosLogin)).willReturn(new DatosJWTToken("thisIsTheToken15975345682"));

        // when: Realización de la petición de login
        ResultActions response = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(datosInputLoginUsuarioJacksonTester.write(datosLogin).getJson()));

        // then: Verificación de la respuesta
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.jwTtoken", is("thisIsTheToken15975345682")));
    }
}