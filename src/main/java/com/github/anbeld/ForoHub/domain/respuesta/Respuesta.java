package com.github.anbeld.ForoHub.domain.respuesta;

import com.github.anbeld.ForoHub.domain.topico.Topico;
import com.github.anbeld.ForoHub.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "respuestas")
@Entity(name = "Respuesta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Respuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "topico_id")
    private Topico topico;

    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    private String respuesta;

    @Setter
    private String url;

    public Respuesta(String respuesta, Usuario autor, Topico topico) {
        this.topico = topico;
        this.fechaCreacion = LocalDateTime.now();
        this.autor = autor;
        this.respuesta = respuesta;
    }
}
