package com.nowaste.backend.dto.response;

import com.nowaste.backend.domain.enums.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@Builder
public class ResiduoSummaryDTO {
    private Long id;
    private String nome;
    private CategoriaResiduo categoria;
    private ClassePericulosidade classePericulosidade;
    private String localizacao;
    private LocalDate prazoDisponibilidade;
    private TipoOferta tipoOferta;
    private BigDecimal preco;
    private TipoFrete frete;
    private Integer quantidadeKg;
    private StatusResiduo status;
    private OffsetDateTime createdAt;
}
