CREATE TABLE residuos (
    id                    BIGSERIAL    PRIMARY KEY,
    nome                  VARCHAR(255) NOT NULL,
    categoria             VARCHAR(50)  NOT NULL,
    composicao_quimica    TEXT,
    classe_periculosidade VARCHAR(10)  NOT NULL,
    localizacao           VARCHAR(255) NOT NULL,
    prazo_disponibilidade DATE         NOT NULL,
    tipo_oferta           VARCHAR(10)  NOT NULL,
    preco                 NUMERIC(12, 2),
    frete                 VARCHAR(20)  NOT NULL,
    quantidade_kg         INTEGER      NOT NULL DEFAULT 0,
    status                VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    aceite_termos         BOOLEAN      NOT NULL DEFAULT FALSE,
    empresa_id            BIGINT,
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
