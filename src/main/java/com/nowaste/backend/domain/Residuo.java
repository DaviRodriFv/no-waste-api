package com.nowaste.backend.domain;

import com.nowaste.backend.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residuos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Residuo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoriaResiduo categoria;

    @Column(name = "composicao_quimica", columnDefinition = "TEXT")
    private String composicaoQuimica;

    @Enumerated(EnumType.STRING)
    @Column(name = "classe_periculosidade", nullable = false, length = 10)
    private ClassePericulosidade classePericulosidade;

    @Column(nullable = false)
    private String localizacao;

    @Column(name = "prazo_disponibilidade", nullable = false)
    private LocalDate prazoDisponibilidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_oferta", nullable = false, length = 10)
    private TipoOferta tipoOferta;

    @Column(precision = 12, scale = 2)
    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoFrete frete;

    @Builder.Default
    @Column(name = "quantidade_kg", nullable = false)
    private Integer quantidadeKg = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusResiduo status = StatusResiduo.ATIVO;

    @Builder.Default
    @Column(name = "aceite_termos", nullable = false)
    private boolean aceiteTermos = false;

    @Column(name = "empresa_id")
    private Long empresaId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "residuo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoResiduo> documentos = new ArrayList<>();
}
