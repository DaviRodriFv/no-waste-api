CREATE TABLE documentos_residuo (
    id             BIGSERIAL    PRIMARY KEY,
    residuo_id     BIGINT       NOT NULL REFERENCES residuos(id) ON DELETE CASCADE,
    tipo_documento VARCHAR(50)  NOT NULL,
    nome_arquivo   VARCHAR(255) NOT NULL,
    caminho        VARCHAR(500) NOT NULL,
    content_type   VARCHAR(100) NOT NULL,
    tamanho_bytes  BIGINT       NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_documentos_residuo_residuo_id ON documentos_residuo(residuo_id);
