INSERT INTO residuos (nome, categoria, composicao_quimica, classe_periculosidade, localizacao,
                      prazo_disponibilidade, tipo_oferta, preco, frete, quantidade_kg, status, aceite_termos)
VALUES
    ('Escória de alto-forno',
     'SOLIDO', 'SiO2, CaO, Al2O3, MgO', 'CLASSE_IIB',
     'São Paulo, SP', '2026-12-31', 'DOACAO', NULL, 'NEGOCIAR', 5000, 'ATIVO', TRUE),

    ('Resíduo químico classe 1',
     'EFLUENTE_QUIMICO', 'Solventes halogenados, metais pesados', 'CLASSE_I',
     'Campinas, SP', '2026-09-30', 'VENDA', 1500.00, 'OFERTANTE', 800, 'ATIVO', TRUE),

    ('Cinzas industriais',
     'SOLIDO', 'SiO2, Al2O3, Fe2O3, CaO', 'CLASSE_IIA',
     'Belo Horizonte, MG', '2026-11-15', 'DOACAO', NULL, 'NEGOCIAR', 10000, 'ATIVO', TRUE),

    ('Resíduo de construção civil',
     'CONSTRUCAO_CIVIL', 'Concreto, argamassa, tijolos', 'CLASSE_IIB',
     'Rio de Janeiro, RJ', '2026-10-31', 'VENDA', 200.00, 'OFERTANTE', 15000, 'ATIVO', TRUE),

    ('Lodo de tratamento',
     'EFLUENTE_QUIMICO', 'Biomassa, compostos orgânicos, água', 'CLASSE_IIA',
     'Curitiba, PR', '2026-08-31', 'DOACAO', NULL, 'NEGOCIAR', 3000, 'ATIVO', TRUE),

    ('Sucata metálica',
     'SOLIDO', 'Fe, Cu, Al, aço carbono', 'CLASSE_IIB',
     'Porto Alegre, RS', '2026-12-15', 'VENDA', 850.00, 'NEGOCIAR', 7500, 'ATIVO', TRUE);
