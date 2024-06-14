package com.github.anbeld.ForoHub.domain.usuario;

import com.github.anbeld.ForoHub.domain.curso.Curso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails{
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

    public Usuario(DatosInputRegistrarUsuario datos, Perfil perfil, String password) {
        this.userName = datos.userName();
        this.email = datos.email();
        this.password = password;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Agregar roles según el perfil del usuario
        switch (userRole) {
            case ESTUDIANTE:
                authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
                break;
            case DOCENTE:
                authorities.add(new SimpleGrantedAuthority("ROLE_DOCENTE"));
                break;
            default:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public String getUserName() {
        return this.userName;
    }
}
