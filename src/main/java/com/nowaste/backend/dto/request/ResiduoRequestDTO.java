package com.nowaste.backend.dto.request;

import com.nowaste.backend.domain.enums.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ResiduoRequestDTO {

    @NotBlank
    private String nome;

    @NotNull
    private CategoriaResiduo categoria;

    private String composicaoQuimica;

    @NotNull
    private ClassePericulosidade classePericulosidade;

    @NotBlank
    private String localizacao;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate prazoDisponibilidade;

    @NotNull
    private TipoOferta tipoOferta;

    private BigDecimal preco;

    @NotNull
    private TipoFrete frete;

    @NotNull
    @Min(0)
    private Integer quantidadeKg;

    private boolean aceitouTermos;
}
