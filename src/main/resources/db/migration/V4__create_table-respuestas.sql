CREATE TABLE IF NOT EXISTS respuestas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    topico_id BIGINT NOT NULL,
    fechaCreacion DATETIME NOT NULL,
    autor_id BIGINT NOT NULL,
    respuesta VARCHAR(2000),

    PRIMARY KEY (id),

    CONSTRAINT fk_respuestas_autor_id FOREIGN KEY (autor_id) REFERENCES usuarios(id),
    CONSTRAINT fk_respuestas_topico_id FOREIGN KEY (topico_id) REFERENCES topicos(id)
);
