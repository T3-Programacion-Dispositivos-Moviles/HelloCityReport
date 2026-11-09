-- Esquema de la base de datos local de CityReport (SQLite).
-- Corresponde al diagrama ER de esta carpeta. En la app estas tablas las crea Room
-- a partir de las clases @Entity; este script sirve para revisar el modelo.

PRAGMA foreign_keys = ON;

CREATE TABLE usuario (
    id_usuario      INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre          TEXT    NOT NULL,
    correo          TEXT    NOT NULL UNIQUE,
    telefono        TEXT,
    rol             TEXT    NOT NULL DEFAULT 'CIUDADANO'
                            CHECK (rol IN ('CIUDADANO', 'ADMIN')),
    fecha_registro  INTEGER NOT NULL
);

CREATE TABLE categoria (
    id_categoria    INTEGER PRIMARY KEY AUTOINCREMENT,
    clave           TEXT    NOT NULL UNIQUE,
    nombre          TEXT    NOT NULL UNIQUE,
    descripcion     TEXT,
    icono           TEXT
);

CREATE TABLE incidencia (
    id_incidencia   INTEGER PRIMARY KEY AUTOINCREMENT,
    id_usuario      INTEGER NOT NULL,
    id_categoria    INTEGER NOT NULL,
    titulo          TEXT    NOT NULL,
    descripcion     TEXT    NOT NULL,
    estado          TEXT    NOT NULL DEFAULT 'PENDIENTE'
                            CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'RESUELTO')),
    ubicacion       TEXT    NOT NULL,
    latitud         REAL,
    longitud        REAL,
    foto_ruta       TEXT,
    fecha_creacion  INTEGER NOT NULL,
    FOREIGN KEY (id_usuario)   REFERENCES usuario (id_usuario)     ON DELETE CASCADE,
    FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria) ON DELETE RESTRICT
);

CREATE INDEX idx_incidencia_usuario   ON incidencia (id_usuario);
CREATE INDEX idx_incidencia_categoria ON incidencia (id_categoria);

-- Categorias iniciales (las mismas del enum Categoria de la app)
-- (icono queda nulo hasta que se agreguen los iconos de cada categoria)
INSERT INTO categoria (clave, nombre, descripcion) VALUES
    ('BACHE',     'Bache',                 'Hoyos o daños en el pavimento'),
    ('LUMINARIA', 'Luminaria',             'Postes de luz apagados o dañados'),
    ('BASURA',    'Acumulación de basura', 'Basura sin recoger en la vía pública'),
    ('FUGA_AGUA', 'Fuga de agua',          'Fugas en tomas, tuberías o banquetas'),
    ('OTRO',      'Otro',                  'Cualquier otro problema urbano');

-- Ejemplo: incidencias con el nombre de su categoria y de quien las reporto
-- SELECT i.titulo, c.nombre AS categoria, u.nombre AS usuario, i.estado
-- FROM incidencia i
-- JOIN categoria c ON c.id_categoria = i.id_categoria
-- JOIN usuario   u ON u.id_usuario   = i.id_usuario
-- ORDER BY i.fecha_creacion DESC;
