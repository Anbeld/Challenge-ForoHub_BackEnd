package com.github.anbeld.ForoAlura.domain.usuario;

import com.github.anbeld.ForoAlura.domain.curso.Curso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    @Column(unique = true)
    @Email
    private String email;

    private String password;
    @Getter
    private boolean status;

    @Enumerated(EnumType.STRING)
    private Perfil userRole;

    @ManyToMany(mappedBy = "estudiantes")
    private List<Curso> cursosEstudiante = new ArrayList<>();

    @OneToMany(mappedBy = "docente")
    private List<Curso> cursosDocente = new ArrayList<>();

    public Usuario(DatosInputRegistrarUsuario datos, Perfil perfil) {
        this.userName = datos.userName();
        this.email = datos.email();
        this.password = datos.password();
        this.status = true;
        this.userRole = perfil;
    }

    public void actualizarPassword(DatosInputActualizarPasswordUsuario datos){
        if (!datos.newPassword().equalsIgnoreCase("null")){
            this.password = datos.newPassword();
        }
    }

    public void desactivarUsuario() {
        this.status = false;
    }
}
