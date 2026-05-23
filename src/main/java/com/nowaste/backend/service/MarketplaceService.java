package com.nowaste.backend.service;

import com.nowaste.backend.domain.Residuo;
import com.nowaste.backend.domain.enums.*;
import com.nowaste.backend.dto.response.MarketplaceItemDTO;
import com.nowaste.backend.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final ResiduoRepository residuoRepository;
    private final ResiduoService residuoService;

    @Transactional(readOnly = true)
    public Page<MarketplaceItemDTO> listar(CategoriaResiduo categoria, ClassePericulosidade classePericulosidade,
                                            TipoOferta tipoOferta, BigDecimal precoMin, BigDecimal precoMax,
                                            Integer quantidadeMin, String search, Pageable pageable) {
        Specification<Residuo> spec = residuoService.buildSpec(
                categoria, classePericulosidade, tipoOferta,
                StatusResiduo.ATIVO, precoMin, precoMax, quantidadeMin, search, Boolean.TRUE);
        return residuoRepository.findAll(spec, pageable).map(this::toMarketplaceDTO);
    }

    private MarketplaceItemDTO toMarketplaceDTO(Residuo r) {
        return MarketplaceItemDTO.builder()
                .id(r.getId())
                .nome(r.getNome())
                .categoria(r.getCategoria())
                .classePericulosidade(r.getClassePericulosidade())
                .localizacao(r.getLocalizacao())
                .tipoOferta(r.getTipoOferta())
                .preco(r.getPreco())
                .frete(r.getFrete())
                .quantidadeKg(r.getQuantidadeKg())
                // TODO: implementar via algoritmo de matching
                .compatibilidade(ThreadLocalRandom.current().nextInt(70, 100))
                // TODO: implementar via geolocalização
                .distanciaKm(ThreadLocalRandom.current().nextInt(5, 101))
                .createdAt(r.getCreatedAt())
                .build();
    }
}
