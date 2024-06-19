CREATE TABLE IF NOT EXISTS respuestas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topico_id BIGINT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    autor_id BIGINT NOT NULL,
    respuesta VARCHAR(2000),
    url VARCHAR(300),

    PRIMARY KEY (id),

    CONSTRAINT fk_respuestas_autor_id FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    CONSTRAINT fk_respuestas_topico_id FOREIGN KEY (topico_id) REFERENCES topicos(id)
);
