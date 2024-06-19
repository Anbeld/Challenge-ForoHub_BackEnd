package com.github.anbeld.ForoHub.domain.usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    // Datos de prueba
    private Usuario estudianteTest;
    private Usuario docenteTest;

    // Configurar datos de prueba antes de cada test
    @BeforeEach
    void setUp() {
        estudianteTest = Usuario.builder()
                .userName("Pablo Estudiante")
                .email("pablo.estudiante@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.ESTUDIANTE)
                .build();

        docenteTest = Usuario.builder()
                .userName("Maria Docente")
                .email("maria.docente@foro.com")
                .password("password")
                .status(true)
                .userRole(Perfil.DOCENTE)
                .build();
    }

    @DisplayName("Test - Registrar un nuevo usuario")
    @Test
    void registrarUsuario() {
        // given: Preparación de datos
        // Se define un nuevo estudiante y un nuevo docente
        Usuario nuevoEstudiante = estudianteTest;
        Usuario nuevoDocente = docenteTest;

        // when: Ejecución de la acción a probar
        // Se registra el nuevo estudiante en el repositorio de usuarios
        Usuario estudianteRegistrado = repository.save(nuevoEstudiante);

        // Se registra el nuevo docente en el repositorio de usuarios
        Usuario docenteRegistrado = repository.save(nuevoDocente);

        // then: Verificación de los resultados esperados
        // Se verifica que el estudiante registrado no sea nulo y que tenga un ID mayor que 0
        assertThat(estudianteRegistrado).isNotNull();
        assertThat(estudianteRegistrado.getId()).isGreaterThan(0);

        // Se verifica que el ID del estudiante registrado no sea igual al ID del docente registrado
        // Esto asegura que no existan dos usuarios con el mismo ID en el sistema
        assertThat(estudianteRegistrado.getId()).isNotEqualTo(docenteRegistrado.getId());

        // Se verifica que el docente registrado no sea nulo y que tenga un ID mayor que 0
        assertThat(docenteRegistrado).isNotNull();
        assertThat(docenteRegistrado.getId()).isGreaterThan(0);
    }


    @DisplayName("Test - Obtener usuario por id")
    @Test
    void findById() {
        // given: Preparación de datos
        // Se registran un estudiante y un docente en el repositorio de usuarios
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        // Se obtienen los IDs de los usuarios registrados
        Long estudianteId = estudiante.getId();
        Long docenteId = docente.getId();

        // when: Ejecución de la acción a probar
        // Se busca el estudiante por su ID en el repositorio
        Optional<Usuario> estudianteRegistrado = repository.findById(estudianteId);

        // Se busca el docente por su ID en el repositorio
        Optional<Usuario> docenteRegistrado = repository.findById(docenteId);

        // then: Verificación de los resultados esperados
        // Se verifica que el estudiante encontrado sea igual al estudiante registrado previamente
        assertThat(estudiante).isEqualTo(estudianteRegistrado.get());

        // Se verifica que el docente encontrado sea igual al docente registrado previamente
        assertThat(docente).isEqualTo(docenteRegistrado.get());
    }

    @DisplayName("Test - Obtener usuario por id y perfil")
    @Test
    void obtenerUsuarioPorIdYPerfil() {
        // given: Preparación de datos
        // Se registran un estudiante y un docente en el repositorio de usuarios
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        // Se obtienen los IDs y perfiles de los usuarios registrados
        Long estudianteId = estudiante.getId();
        Long docenteId = docente.getId();
        Perfil estudiantePerfil = estudiante.getUserRole();
        Perfil docentePerfil = docente.getUserRole();

        // when: Ejecución de la acción a probar
        // Se busca el usuario por su ID y perfil (estudiante) en el repositorio
        Optional<Usuario> estudianteRegistrado = repository.obtenerUsuarioPorIdYPerfil(estudianteId, estudiantePerfil);

        // Se busca el usuario por su ID y perfil (docente) en el repositorio
        Optional<Usuario> docenteRegistrado = repository.obtenerUsuarioPorIdYPerfil(docenteId, docentePerfil);

        // then: Verificación de los resultados esperados
        // Se verifica que el estudiante encontrado sea igual al estudiante registrado previamente
        assertThat(estudiante).isEqualTo(estudianteRegistrado.get());

        // Se verifica que el docente encontrado sea igual al docente registrado previamente
        assertThat(docente).isEqualTo(docenteRegistrado.get());
    }

    @DisplayName("Test - Obtener un objeto UserDetails por email")
    @Test
    void findByEmail() {
        // given: Preparación de datos
        // Se registran un estudiante y un docente en el repositorio de usuarios
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        // Se obtienen los emails de los usuarios registrados
        String estudianteEmail = estudiante.getEmail();
        String docenteEmail = docente.getEmail();

        // when: Ejecución de la acción a probar
        // Se busca el UserDetails del estudiante por su email en el repositorio
        UserDetails estudianteRegistrado = repository.findByEmail(estudianteEmail);

        // Se busca el UserDetails del docente por su email en el repositorio
        UserDetails docenteRegistrado = repository.findByEmail(docenteEmail);

        // then: Verificación de los resultados esperados
        // Se verifica que el UserDetails del estudiante encontrado sea igual al estudiante registrado previamente
        assertThat(estudiante).isEqualTo(estudianteRegistrado);

        // Se verifica que el UserDetails del docente encontrado sea igual al docente registrado previamente
        assertThat(docente).isEqualTo(docenteRegistrado);
    }

    @DisplayName("Test - Obtener un usuario por email")
    @Test
    void obtenerUsuarioPorEmail() {
        // given
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        String estudianteEmail = estudiante.getEmail();
        String docenteEmail = docente.getEmail();

        // when
        Optional<Usuario> estudianteRegistrado = repository.obtenerUsuarioPorEmail(estudianteEmail);
        Optional<Usuario> docenteRegistrado = repository.obtenerUsuarioPorEmail(docenteEmail);

        // then
        assertThat(estudiante).isEqualTo(estudianteRegistrado.get());
        assertThat(docente).isEqualTo(docenteRegistrado.get());
    }

    @DisplayName("Test - Obtener todos los usuarios activos por perfil")
    @Test
    void obtenerUsuariosPorStatusActivoYPerfil() {
        // given: Preparación de datos
        // Se registran un estudiante y un docente en el repositorio de usuarios
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        // Se obtienen los emails de los usuarios registrados
        String estudianteEmail = estudiante.getEmail();
        String docenteEmail = docente.getEmail();

        // when: Ejecución de la acción a probar
        // Se busca el usuario del estudiante por su email en el repositorio
        Optional<Usuario> estudianteRegistrado = repository.obtenerUsuarioPorEmail(estudianteEmail);

        // Se busca el usuario del docente por su email en el repositorio
        Optional<Usuario> docenteRegistrado = repository.obtenerUsuarioPorEmail(docenteEmail);

        // then: Verificación de los resultados esperados
        // Se verifica que el usuario del estudiante encontrado sea igual al estudiante registrado previamente
        assertThat(estudiante).isEqualTo(estudianteRegistrado.get());

        // Se verifica que el usuario del docente encontrado sea igual al docente registrado previamente
        assertThat(docente).isEqualTo(docenteRegistrado.get());
    }

    @DisplayName("Test - Verificar si un usuario cuenta con el email y password suministrados por el usuario")
    @Test
    void verificarUsuarioPorEmailPasswordPerfil() {
        // given: Preparación de datos
        // Se registra un estudiante y un docente en el repositorio de usuarios
        Usuario estudiante = repository.save(estudianteTest);
        Usuario docente = repository.save(docenteTest);

        // Se obtienen los emails y passwords de los usuarios registrados
        String estudianteEmail = estudiante.getEmail();
        String docenteEmail = docente.getEmail();

        String estudiantePassword = estudiante.getPassword();
        String docentePassword = docente.getPassword();

        // Se obtienen los perfiles de los usuarios registrados
        Perfil estudiantePerfil = estudiante.getUserRole();
        Perfil docentePerfil = docente.getUserRole();

        // when: Ejecución de la acción a probar
        // Se verifica si el usuario del estudiante existe en el repositorio y sus credenciales son válidas
        Optional<Usuario> estudianteRegistrado = repository.verificarUsuarioPorEmailPasswordPerfil(estudianteEmail, estudiantePassword, estudiantePerfil);

        // Se verifica si el usuario del docente existe en el repositorio y sus credenciales son válidas
        Optional<Usuario> docenteRegistrado = repository.verificarUsuarioPorEmailPasswordPerfil(docenteEmail, docentePassword, docentePerfil);

        // then: Verificación de los resultados esperados
        // Se verifica que el usuario del estudiante encontrado sea igual al estudiante registrado previamente
        assertThat(estudiante).isEqualTo(estudianteRegistrado.get());

        // Se verifica que el usuario del docente encontrado sea igual al docente registrado previamente
        assertThat(docente).isEqualTo(docenteRegistrado.get());
    }
}