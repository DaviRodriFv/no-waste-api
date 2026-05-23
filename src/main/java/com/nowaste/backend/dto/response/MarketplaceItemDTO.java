package com.nowaste.backend.dto.response;

import com.nowaste.backend.domain.enums.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class MarketplaceItemDTO {
    private Long id;
    private String nome;
    private CategoriaResiduo categoria;
    private ClassePericulosidade classePericulosidade;
    private String localizacao;
    private TipoOferta tipoOferta;
    private BigDecimal preco;
    private TipoFrete frete;
    private Integer quantidadeKg;
    // TODO: implementar via algoritmo de matching
    private Integer compatibilidade;
    // TODO: implementar via geolocalização
    private Integer distanciaKm;
    private OffsetDateTime createdAt;
}
