CREATE TABLE IF NOT EXISTS topicos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(300) NOT NULL,
    mensaje VARCHAR(2000) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    resuelto TINYINT NOT NULL,
    url VARCHAR(300),
    autor_id BIGINT NOT NULL,
    curso_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_topicos_autor_id FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    CONSTRAINT fk_topicos_curso_id FOREIGN KEY (curso_id) REFERENCES cursos(id)
);
