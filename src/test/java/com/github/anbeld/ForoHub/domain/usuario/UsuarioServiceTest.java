package com.github.anbeld.ForoHub.domain.usuario;

import com.github.anbeld.ForoHub.infra.errores.ValidacionDeIntegridad;
import com.github.anbeld.ForoHub.infra.security.DatosJWTToken;
import com.github.anbeld.ForoHub.infra.security.JWTService;
import lombok.SneakyThrows;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    // Datos de prueba
    private Usuario estudianteTest1;
    private Usuario estudianteTest2;
    private Usuario estudianteTest3;
    private Usuario docenteTest1;
    private Usuario docenteTest2;
    private Usuario docenteTest3;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        estudianteTest1 = Usuario.builder()
                .id(1L)
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .build();

        estudianteTest2 = Usuario.builder()
                .id(2L)
                .userName("Pedro Estudiante2")
                .email("pedro.estudiante2@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .build();

        estudianteTest3 = Usuario.builder()
                .id(3L)
                .userName("Fernando Estudiante3")
                .email("fernando.estudiante3@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .build();

        docenteTest1 = Usuario.builder()
                .id(2L)
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .build();
        docenteTest2 = Usuario.builder()
                .id(2L)
                .userName("Pedro Estudiante2")
                .email("pedro.estudiante2@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .build();

        docenteTest3 = Usuario.builder()
                .id(3L)
                .userName("Fernando Estudiante3")
                .email("fernando.estudiante3@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .build();
    }

    @DisplayName("Test - Iniciar seción estudiante")
    @Test
    void loginEstudiante() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el estudiante
        given(repository.obtenerUsuarioPorEmail(estudianteTest1.getEmail())).willReturn(Optional.of(estudianteTest1));
        // Se configura el comportamiento del servicio JWT para generar un token
        given(jwtService.generateToken(estudianteTest1.getEmail(), estudianteTest1.getUserRole())).willReturn("esteEsElTokenGenerado");

        // when: Ejecución de la acción a probar
        // Se realiza el inicio de sesión del estudiante y se obtiene el token generado
        DatosJWTToken token = service.login(new DatosInputLoginUsuario(estudianteTest1.getEmail(), estudianteTest1.getPassword()));

        // then: Verificación de los resultados esperados
        // Se verifica que el token generado sea el esperado
        assertThat(token.jwTtoken()).isEqualTo("esteEsElTokenGenerado");
    }

    @DisplayName("Test - Iniciar seción docente")
    @Test
    void loginDocente() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver el docente
        given(repository.obtenerUsuarioPorEmail(docenteTest1.getEmail())).willReturn(Optional.of(docenteTest1));
        // Se configura el comportamiento del servicio JWT para generar un token
        given(jwtService.generateToken(docenteTest1.getEmail(), docenteTest1.getUserRole())).willReturn("esteEsElTokenGenerado");

        // when: Ejecución de la acción a probar
        // Se realiza el inicio de sesión del docente y se obtiene el token generado
        DatosJWTToken token = service.login(new DatosInputLoginUsuario(docenteTest1.getEmail(), docenteTest1.getPassword()));

        // then: Verificación de los resultados esperados
        // Se verifica que el token generado sea el esperado
        assertThat(token.jwTtoken()).isEqualTo("esteEsElTokenGenerado");
    }

    @DisplayName("Test - Registrar un nuevo estudiante")
    @SneakyThrows
    @Test
    void registrarEstudiante() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un usuario nuevo
        given(repository.obtenerUsuarioPorEmail(estudianteTest1.getEmail())).willReturn(Optional.empty());
        given(repository.save(any(Usuario.class))).willReturn(estudianteTest1);

        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar
        // Se registra un nuevo estudiante mediante el servicio y se obtiene el usuario registrado
        Usuario nuevoEstudiante = service.registrarUsuario(
                new DatosInputRegistrarUsuario(estudianteTest1.getUserName(),
                        estudianteTest1.getEmail(), estudianteTest1.getPassword()),
                estudianteTest1.getUserRole(),
                testUrl);

        // then: Verificación de los resultados esperados
        // Se verifica que el nuevo estudiante registrado tenga los mismos datos que el estudiante de prueba
        assertThat(nuevoEstudiante).isNotNull();
        assertThat(nuevoEstudiante.getEmail()).isEqualTo(estudianteTest1.getEmail());
        assertThat(nuevoEstudiante.getUserName()).isEqualTo(estudianteTest1.getUserName());
    }

    @DisplayName("Test - Registrar un nuevo estudiante con ValidaciDeIntegridad")
    @SneakyThrows
    @Test
    void registrarEstudianteConValidacionDeIntegridad() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un usuario existente
        given(repository.obtenerUsuarioPorEmail(estudianteTest1.getEmail())).willReturn(Optional.of(estudianteTest1));

        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar y verificación de excepción esperada
        // Se espera que la acción de registrar un nuevo estudiante lance una ValidacionDeIntegridad
        assertThrows(ValidacionDeIntegridad.class, () -> service.registrarUsuario(
                new DatosInputRegistrarUsuario(estudianteTest1.getUserName(),
                        estudianteTest1.getEmail(), estudianteTest1.getPassword()),
                estudianteTest1.getUserRole(),
                testUrl));

        // then: Verificación de que no se haya guardado ningún usuario en el repositorio
        // Se verifica que el método save del repositorio nunca se haya llamado con cualquier usuario
        verify(repository, never()).save(any(Usuario.class));
    }

    @DisplayName("Test - Registrar un nuevo docente")
    @SneakyThrows
    @Test
    void registrarDocente() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un usuario no existente
        given(repository.obtenerUsuarioPorEmail(docenteTest1.getEmail()))
                .willReturn(Optional.empty());
        // Se configura el comportamiento del mock del repositorio para devolver el docente registrado
        given(repository.save(any(Usuario.class))).willReturn(docenteTest1);

        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar
        // Se registra un nuevo docente
        Usuario nuevoDocente = service.registrarUsuario(
                new DatosInputRegistrarUsuario(docenteTest1.getUserName(),
                        docenteTest1.getEmail(), docenteTest1.getPassword()),
                docenteTest1.getUserRole(),
                testUrl);

        //then: Verificación de que se haya registrado el nuevo docente correctamente
        // Se verifica que el docente registrado no sea nulo y que tenga los datos esperados
        assertThat(nuevoDocente).isNotNull();
        assertThat(nuevoDocente.getEmail()).isEqualTo(docenteTest1.getEmail());
        assertThat(nuevoDocente.getUserName()).isEqualTo(docenteTest1.getUserName());
    }

    @DisplayName("Test - Registrar un nuevo docente con ValidaciDeIntegridad")
    @SneakyThrows
    @Test
    void registrarDocenteConValidacionDeIntegridad() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Se configura el comportamiento del mock del repositorio de usuarios para devolver un usuario existente
        given(repository.obtenerUsuarioPorEmail(docenteTest1.getEmail()))
                .willReturn(Optional.of(docenteTest1));

        UriComponentsBuilder testUrl = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");

        // when: Ejecución de la acción a probar y verificación de la excepción lanzada
        // Se espera que al intentar registrar un docente que ya existe se lance una excepción de ValidacionDeIntegridad
        assertThrows(ValidacionDeIntegridad.class, () -> service.registrarUsuario(
                new DatosInputRegistrarUsuario(docenteTest1.getUserName(),
                        docenteTest1.getEmail(), docenteTest1.getPassword()),
                docenteTest1.getUserRole(),
                testUrl));

        // then: Verificación de que no se haya guardado ningún nuevo docente
        // Se verifica que no se haya guardado ningún nuevo docente en el repositorio
        verify(repository, never()).save(any(Usuario.class));
    }

    @DisplayName("Test - Obtener el listado de todos los estudiantes activos")
    @Test
    void listadoEstudiantesActivos() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Crear otros estudiantes para incluir en la lista
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("userName"));

        List<Usuario> estudiantes = new ArrayList<>();
        estudiantes.add(estudianteTest1);
        estudiantes.add(estudianteTest2);
        estudiantes.add(estudianteTest3);

        // Configurar el comportamiento del mock del repositorio para devolver una lista de estudiantes activos
        given(repository.obtenerUsuariosPorStatusActivoYPerfil(paginacion, Perfil.ESTUDIANTE))
                .willReturn(new PageImpl<>(estudiantes, paginacion, estudiantes.size()));

        // when: Ejecución de la acción a probar
        // Obtener la lista de estudiantes activos por perfil
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.ESTUDIANTE);

        //then: Verificación de los resultados
        // Verificar que la respuesta no sea nula y contenga todos los estudiantes activos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(estudiantes.size());
        assertThat(response).contains(new DatosOutputUsuario(estudianteTest1));
        assertThat(response).contains(new DatosOutputUsuario(estudianteTest2));
        assertThat(response).contains(new DatosOutputUsuario(estudianteTest3));
    }

    @DisplayName("Test - Obtener el listado de todos los docentes activos")
    @Test
    void listadoDocentesActivos() {
        // given: Preparación de datos y configuración de comportamiento del mock
        Pageable paginacion = PageRequest.of(0, 10, Sort.by("userName"));

        List<Usuario> docentes = new ArrayList<>();
        docentes.add(docenteTest1);
        docentes.add(docenteTest2);
        docentes.add(docenteTest3);

        // Configurar el comportamiento del mock del repositorio para devolver una lista de docentes activos
        given(repository.obtenerUsuariosPorStatusActivoYPerfil(paginacion, Perfil.DOCENTE))
                .willReturn(new PageImpl<>(docentes, paginacion, docentes.size()));

        // when: Ejecución de la acción a probar
        // Obtener la lista de docentes activos por perfil
        var response = service.listadoUsuariosActivosPorPerfil(paginacion, Perfil.DOCENTE);

        //then: Verificación de los resultados
        // Verificar que la respuesta no sea nula y contenga todos los docentes activos
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(docentes.size());
        assertThat(response).contains(new DatosOutputUsuario(docenteTest1));
        assertThat(response).contains(new DatosOutputUsuario(docenteTest2));
        assertThat(response).contains(new DatosOutputUsuario(docenteTest3));
    }

    @DisplayName("Test - Obtener estudiante por id")
    @Test
    void obtenerEstudiantePorId() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para devolver un estudiante por id y perfil
        given(repository.obtenerUsuarioPorIdYPerfil(estudianteTest1.getId(), estudianteTest1.getUserRole()))
                .willReturn(Optional.of(estudianteTest1));

        // when: Ejecución de la acción a probar
        // Obtener el estudiante por id
        DatosOutputEstudiantePorId estudianteRegistrado = service.obtenerEstudiantePorId(estudianteTest1.getId());

        //then: Verificación de los resultados
        // Verificar que el estudiante obtenido no sea nulo
        assertThat(estudianteRegistrado).isNotNull();
    }

    @DisplayName("Test - Obtener docente por id")
    @Test
    void obtenerDocentePorId() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para devolver un docente por id y perfil
        given(repository.obtenerUsuarioPorIdYPerfil(docenteTest1.getId(), docenteTest1.getUserRole()))
                .willReturn(Optional.of(docenteTest1));

        // when: Ejecución de la acción a probar
        // Obtener el docente por id
        DatosOutputDocentePorId docenteRegistrado = service.obtenerDocentePorId(docenteTest1.getId());

        //then: Verificación de los resultados
        // Verificar que el docente obtenido no sea nulo
        assertThat(docenteRegistrado).isNotNull();
    }

    @DisplayName("Test - Actualizar password estudiante")
    @Test
    void actualizarPasswordEstudiante() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para verificar el usuario por email, password y perfil
        given(repository.verificarUsuarioPorEmailPasswordPerfil(estudianteTest1.getEmail(), estudianteTest1.getPassword(), estudianteTest1.getUserRole()))
                .willReturn(Optional.of(estudianteTest1));

        // Definir la nueva contraseña
        String newPassword = "newPassword";

        // Configurar el comportamiento del encoder de contraseñas para devolver una contraseña encriptada
        given(passwordEncoder.encode(newPassword)).willReturn("encryptedPassword");

        // when: Ejecución de la acción a probar
        // Verificar y actualizar la contraseña del estudiante registrado
        Usuario estudianteRegistrado = repository.verificarUsuarioPorEmailPasswordPerfil(estudianteTest1.getEmail(), estudianteTest1.getPassword(), estudianteTest1.getUserRole()).get();
        String currentPassword = estudianteRegistrado.getPassword(); // Obtener la contraseña actual
        String encryptedPassword = passwordEncoder.encode(newPassword); // Encriptar la nueva contraseña

        estudianteRegistrado.actualizarPassword(encryptedPassword); // Actualizar la contraseña en el objeto

        // then: Verificación de los resultados
        // Verificar que la contraseña encriptada no sea igual a la nueva contraseña ni a la contraseña actual
        assertThat(estudianteRegistrado.getPassword()).isNotEqualTo(newPassword);
        assertThat(estudianteRegistrado.getPassword()).isNotEqualTo(currentPassword);
        assertThat(estudianteRegistrado.getPassword()).isEqualTo("encryptedPassword"); // Verificar que la contraseña se haya actualizado correctamente
    }

    @DisplayName("Test - Actualizar password docente")
    @Test
    void actualizarPasswordDocente() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para verificar el usuario por email, password y perfil
        given(repository.verificarUsuarioPorEmailPasswordPerfil(docenteTest1.getEmail(), docenteTest1.getPassword(), docenteTest1.getUserRole()))
                .willReturn(Optional.of(docenteTest1));

        // Definir la nueva contraseña
        String newPassword = "newPassword";

        // Configurar el comportamiento del encoder de contraseñas para devolver una contraseña encriptada
        given(passwordEncoder.encode(newPassword)).willReturn("encryptedPassword");

        // when: Ejecución de la acción a probar
        // Verificar y actualizar la contraseña del docente registrado
        Usuario docenteRegistrado = repository.verificarUsuarioPorEmailPasswordPerfil(docenteTest1.getEmail(), docenteTest1.getPassword(), docenteTest1.getUserRole()).get();
        String currentPassword = docenteRegistrado.getPassword(); // Obtener la contraseña actual
        String encryptedPassword = passwordEncoder.encode(newPassword); // Encriptar la nueva contraseña

        docenteRegistrado.actualizarPassword(encryptedPassword); // Actualizar la contraseña en el objeto

        // then: Verificación de los resultados
        // Verificar que la contraseña encriptada no sea igual a la nueva contraseña ni a la contraseña actual
        assertThat(docenteRegistrado.getPassword()).isNotEqualTo(newPassword);
        assertThat(docenteRegistrado.getPassword()).isNotEqualTo(currentPassword);
        assertThat(docenteRegistrado.getPassword()).isEqualTo("encryptedPassword"); // Verificar que la contraseña se haya actualizado correctamente
    }

    @DisplayName("Test - Delete lógico de un estudiante")
    @Test
    void desactivarEstudiante() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para devolver el estudiante a desactivar
        given(repository.getReferenceById(estudianteTest1.getId())).willReturn(estudianteTest1);

        // when: Ejecución de la acción a probar
        // Llamar al método para desactivar el estudiante
        service.desactivarUsuario(estudianteTest1.getId(), estudianteTest1.getUserRole());

        // then: Verificación de los resultados
        // Verificar que se haya llamado al método save del repositorio una vez con el estudiante
        verify(repository, times(1)).save(estudianteTest1);

        // Verificar que el estado (status) del estudiante sea false después de la desactivación
        assertThat(estudianteTest1.isStatus()).isEqualTo(false);
    }

    @DisplayName("Test - Delete lógico de un docente")
    @Test
    void desactivarDocente() {
        // given: Preparación de datos y configuración de comportamiento del mock
        // Configurar el comportamiento del mock del repositorio para devolver el docente a desactivar
        given(repository.getReferenceById(docenteTest1.getId())).willReturn(docenteTest1);

        // when: Ejecución de la acción a probar
        // Llamar al método para desactivar el docente
        service.desactivarUsuario(docenteTest1.getId(), docenteTest1.getUserRole());

        // then: Verificación de los resultados
        // Verificar que se haya llamado al método save del repositorio una vez con el docente
        verify(repository, times(1)).save(docenteTest1);

        // Verificar que el estado (status) del docente sea false después de la desactivación
        assertThat(docenteTest1.isStatus()).isEqualTo(false);
    }
}