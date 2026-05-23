package com.nowaste.backend.controller;

import com.nowaste.backend.domain.enums.*;
import com.nowaste.backend.dto.response.MarketplaceItemDTO;
import com.nowaste.backend.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @GetMapping
    public ResponseEntity<Page<MarketplaceItemDTO>> listar(
            @RequestParam(required = false) CategoriaResiduo categoria,
            @RequestParam(required = false) ClassePericulosidade classePericulosidade,
            @RequestParam(required = false) TipoOferta tipoOferta,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Integer quantidadeMin,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, parts[0].trim()));
        return ResponseEntity.ok(marketplaceService.listar(
                categoria, classePericulosidade, tipoOferta,
                precoMin, precoMax, quantidadeMin, search, pageable));
    }
}
