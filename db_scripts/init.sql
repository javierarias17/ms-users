-- Usuario dedicado para el microservicio
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'ms_users_user') THEN
        CREATE USER ms_users_user WITH PASSWORD 'vaca1234';
    END IF;
END
$$;

-- Base de datos del microservicio
CREATE DATABASE ms_users
    WITH OWNER = ms_users_user
    ENCODING = 'UTF8'
    TEMPLATE template0;

-- Conectarse a la base de datos del microservicio
\c ms_users

-- Schema dedicado
CREATE SCHEMA IF NOT EXISTS ms_users;

-- Permisos al usuario sobre el schema
GRANT USAGE  ON SCHEMA ms_users TO ms_users_user;
GRANT CREATE ON SCHEMA ms_users TO ms_users_user;

-- Privilegios para objetos futuros
ALTER DEFAULT PRIVILEGES IN SCHEMA ms_users
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO ms_users_user;

ALTER DEFAULT PRIVILEGES IN SCHEMA ms_users
    GRANT USAGE, SELECT ON SEQUENCES TO ms_users_user;

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS ms_users.users (
    id                    BIGSERIAL    PRIMARY KEY,
    identification_number VARCHAR(20)  NOT NULL,
    name                  VARCHAR(100) NOT NULL,
    email                 VARCHAR(150) NOT NULL
);

-- Acceso a tablas y secuencias existentes
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES    IN SCHEMA ms_users TO ms_users_user;
GRANT USAGE, SELECT                  ON ALL SEQUENCES IN SCHEMA ms_users TO ms_users_user;
