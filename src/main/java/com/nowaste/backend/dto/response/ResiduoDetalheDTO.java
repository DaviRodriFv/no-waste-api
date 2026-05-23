package com.nowaste.backend.dto.response;

import com.nowaste.backend.domain.enums.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ResiduoDetalheDTO {
    private Long id;
    private String nome;
    private CategoriaResiduo categoria;
    private String composicaoQuimica;
    private ClassePericulosidade classePericulosidade;
    private String localizacao;
    private LocalDate prazoDisponibilidade;
    private TipoOferta tipoOferta;
    private BigDecimal preco;
    private TipoFrete frete;
    private Integer quantidadeKg;
    private StatusResiduo status;
    private boolean aceiteTermos;
    private Long empresaId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<DocumentoResponseDTO> documentos;
}
