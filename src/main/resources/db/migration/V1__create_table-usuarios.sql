CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    userName VARCHAR(40) NOT NULL,
    email VARCHAR(40) NOT NULL UNIQUE,
    password VARCHAR(300) NOT NULL,
    status TINYINT NOT NULL,
    url VARCHAR(300),
    userRole VARCHAR(20) NOT NULL,

    PRIMARY KEY (id)
);
