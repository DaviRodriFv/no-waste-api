package com.nowaste.backend.controller;

import com.nowaste.backend.domain.DocumentoResiduo;
import com.nowaste.backend.domain.enums.*;
import com.nowaste.backend.dto.request.PatchStatusDTO;
import com.nowaste.backend.dto.request.ResiduoRequestDTO;
import com.nowaste.backend.dto.response.*;
import com.nowaste.backend.service.ResiduoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/residuos")
@RequiredArgsConstructor
public class ResiduoController {

    private final ResiduoService residuoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResiduoResponseDTO> criar(
            @Valid @ModelAttribute ResiduoRequestDTO dto,
            @RequestParam(required = false) MultipartFile laudoTecnico,
            @RequestParam(required = false) MultipartFile orcamentoDescarte) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(residuoService.criar(dto, laudoTecnico, orcamentoDescarte));
    }

    @GetMapping
    public ResponseEntity<Page<ResiduoSummaryDTO>> listar(
            @RequestParam(required = false) CategoriaResiduo categoria,
            @RequestParam(required = false) ClassePericulosidade classePericulosidade,
            @RequestParam(required = false) TipoOferta tipoOferta,
            @RequestParam(required = false) StatusResiduo status,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax,
            @RequestParam(required = false) Integer quantidadeMin,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        return ResponseEntity.ok(residuoService.listar(
                categoria, classePericulosidade, tipoOferta, status,
                precoMin, precoMax, quantidadeMin, search, buildPageable(page, size, sort)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResiduoDetalheDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(residuoService.buscarPorId(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResiduoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute ResiduoRequestDTO dto,
            @RequestParam(required = false) MultipartFile laudoTecnico,
            @RequestParam(required = false) MultipartFile orcamentoDescarte) {
        return ResponseEntity.ok(residuoService.atualizar(id, dto, laudoTecnico, orcamentoDescarte));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ResiduoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody PatchStatusDTO dto) {
        return ResponseEntity.ok(residuoService.atualizarStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        residuoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/documentos")
    public ResponseEntity<List<DocumentoResponseDTO>> listarDocumentos(@PathVariable Long id) {
        return ResponseEntity.ok(residuoService.listarDocumentos(id));
    }

    @GetMapping("/{id}/documentos/{docId}/download")
    public ResponseEntity<Resource> downloadDocumento(
            @PathVariable Long id,
            @PathVariable Long docId) {
        DocumentoResiduo doc = residuoService.buscarDocumento(id, docId);
        Path caminho = residuoService.resolverCaminhoDocumento(id, docId);
        Resource resource = new PathResource(caminho);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getNomeArquivo() + "\"")
                .contentType(MediaType.parseMediaType(doc.getContentType()))
                .body(resource);
    }

    @DeleteMapping("/{id}/documentos/{docId}")
    public ResponseEntity<Void> deletarDocumento(
            @PathVariable Long id,
            @PathVariable Long docId) {
        residuoService.deletarDocumento(id, docId);
        return ResponseEntity.noContent().build();
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, field));
    }
}
