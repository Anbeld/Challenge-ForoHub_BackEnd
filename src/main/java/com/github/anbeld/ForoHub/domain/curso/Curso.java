package com.github.anbeld.ForoHub.domain.curso;

import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "cursos")
@Entity(name = "Curso")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    private Integer numeroEstudiantes;

    @Setter
    private String url;

    @Setter
    @ManyToOne
    @JoinColumn(name = "docente_id")
    private Usuario docente;

    @ManyToMany()
    @JoinTable(
            name = "cursos_estudiantes",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private List<Usuario> estudiantes = new ArrayList<>();

    public Curso(DatosInputRegistrarCurso datos, Usuario docente) {
        this.nombre =  datos.nombre();
        this.categoria =  datos.categoria();
        this.numeroEstudiantes = 0;
        this.docente = docente;
        docente.getCursosDocente().add(this);
    }

    public void registrarEstudiante(Usuario estudiante){
        this.numeroEstudiantes += 1;
        this.estudiantes.add(estudiante);
        estudiante.getCursosEstudiante().add(this);
    }
}