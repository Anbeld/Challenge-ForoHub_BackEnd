CREATE TABLE IF NOT EXISTS cursos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(20) NOT NULL,
    numero_estudiantes INTEGER NOT NULL,
    url VARCHAR(300),
    docente_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_cursos_docente_id FOREIGN KEY (docente_id) REFERENCES usuarios(id)
);
