CREATE TABLE IF NOT EXISTS cursos_estudiantes (
    curso_id BIGINT,
    estudiante_id BIGINT,

    PRIMARY KEY (curso_id, estudiante_id),

    CONSTRAINT fk_curso_estudiantes_curso_id FOREIGN KEY (curso_id) REFERENCES cursos(id),
    CONSTRAINT fk_curso_estudiantes_estudiante_id FOREIGN KEY (estudiante_id) REFERENCES usuarios(id)
);