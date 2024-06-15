package com.github.anbeld.ForoHub.domain.topico;

import com.github.anbeld.ForoHub.domain.curso.Curso;
import com.github.anbeld.ForoHub.domain.respuesta.Respuesta;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "topicos")
@Entity(name = "Topico")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private boolean resuelto;

    @Setter
    private String url;

    @ManyToOne()
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @OneToMany(mappedBy = "topico")
    private List<Respuesta> respuestas;

    public Topico(DatosInputTopico datos, Usuario autor, Curso curso) {
        this.titulo = datos.titulo();
        this.mensaje = datos.mensaje();
        this.fechaCreacion = LocalDateTime.now();
        this.resuelto = false;
        this.autor = autor;
        this.curso = curso;
    }

    public void cerrarTopico() {
        this.resuelto = true;
    }
}
