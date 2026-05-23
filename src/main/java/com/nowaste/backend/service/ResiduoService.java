package com.nowaste.backend.service;

import com.nowaste.backend.domain.DocumentoResiduo;
import com.nowaste.backend.domain.Residuo;
import com.nowaste.backend.domain.enums.*;
import com.nowaste.backend.dto.request.PatchStatusDTO;
import com.nowaste.backend.dto.request.ResiduoRequestDTO;
import com.nowaste.backend.dto.response.*;
import com.nowaste.backend.exception.BadRequestException;
import com.nowaste.backend.exception.EntityNotFoundException;
import com.nowaste.backend.repository.DocumentoResiduoRepository;
import com.nowaste.backend.repository.ResiduoRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResiduoService {

    private final ResiduoRepository residuoRepository;
    private final DocumentoResiduoRepository documentoRepository;
    private final FileStorageService fileStorageService;

    // TODO: adicionar @PreAuthorize quando auth for implementado
    @Transactional
    public ResiduoResponseDTO criar(ResiduoRequestDTO dto, MultipartFile laudoTecnico, MultipartFile orcamentoDescarte) {
        if (!dto.isAceitouTermos()) {
            throw new BadRequestException("aceitouTermos deve ser true para publicar o resíduo");
        }

        Residuo residuo = Residuo.builder()
                .nome(dto.getNome())
                .categoria(dto.getCategoria())
                .composicaoQuimica(dto.getComposicaoQuimica())
                .classePericulosidade(dto.getClassePericulosidade())
                .localizacao(dto.getLocalizacao())
                .prazoDisponibilidade(dto.getPrazoDisponibilidade())
                .tipoOferta(dto.getTipoOferta())
                .preco(dto.getPreco())
                .frete(dto.getFrete())
                .quantidadeKg(dto.getQuantidadeKg())
                .aceiteTermos(true)
                .status(StatusResiduo.ATIVO)
                .build();

        residuo = residuoRepository.save(residuo);
        anexarDocumentos(residuo, laudoTecnico, orcamentoDescarte);
        residuo = residuoRepository.save(residuo);

        return toResponseDTO(residuo);
    }

    @Transactional(readOnly = true)
    public Page<ResiduoSummaryDTO> listar(CategoriaResiduo categoria, ClassePericulosidade classePericulosidade,
                                           TipoOferta tipoOferta, StatusResiduo status,
                                           BigDecimal precoMin, BigDecimal precoMax,
                                           Integer quantidadeMin, String search,
                                           Pageable pageable) {
        StatusResiduo effectiveStatus = status != null ? status : StatusResiduo.ATIVO;
        Specification<Residuo> spec = buildSpec(categoria, classePericulosidade, tipoOferta,
                effectiveStatus, precoMin, precoMax, quantidadeMin, search, null);
        return residuoRepository.findAll(spec, pageable).map(this::toSummaryDTO);
    }

    @Transactional(readOnly = true)
    public ResiduoDetalheDTO buscarPorId(Long id) {
        return toDetalheDTO(findById(id));
    }

    // TODO: adicionar @PreAuthorize quando auth for implementado
    @Transactional
    public ResiduoResponseDTO atualizar(Long id, ResiduoRequestDTO dto,
                                         MultipartFile laudoTecnico, MultipartFile orcamentoDescarte) {
        Residuo residuo = findById(id);

        residuo.setNome(dto.getNome());
        residuo.setCategoria(dto.getCategoria());
        residuo.setComposicaoQuimica(dto.getComposicaoQuimica());
        residuo.setClassePericulosidade(dto.getClassePericulosidade());
        residuo.setLocalizacao(dto.getLocalizacao());
        residuo.setPrazoDisponibilidade(dto.getPrazoDisponibilidade());
        residuo.setTipoOferta(dto.getTipoOferta());
        residuo.setPreco(dto.getPreco());
        residuo.setFrete(dto.getFrete());
        residuo.setQuantidadeKg(dto.getQuantidadeKg());
        if (dto.isAceitouTermos()) {
            residuo.setAceiteTermos(true);
        }

        anexarDocumentos(residuo, laudoTecnico, orcamentoDescarte);
        return toResponseDTO(residuoRepository.save(residuo));
    }

    // TODO: adicionar @PreAuthorize quando auth for implementado
    @Transactional
    public ResiduoResponseDTO atualizarStatus(Long id, PatchStatusDTO dto) {
        Residuo residuo = findById(id);
        residuo.setStatus(dto.getStatus());
        return toResponseDTO(residuoRepository.save(residuo));
    }

    // TODO: adicionar @PreAuthorize quando auth for implementado
    @Transactional
    public void deletar(Long id) {
        Residuo residuo = findById(id);
        residuo.setStatus(StatusResiduo.INATIVO);
        residuoRepository.save(residuo);
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponseDTO> listarDocumentos(Long residuoId) {
        findById(residuoId);
        return documentoRepository.findByResiduoId(residuoId).stream()
                .map(this::toDocumentoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentoResiduo buscarDocumento(Long residuoId, Long docId) {
        findById(residuoId);
        return documentoRepository.findByIdAndResiduoId(docId, residuoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));
    }

    public Path resolverCaminhoDocumento(Long residuoId, Long docId) {
        DocumentoResiduo doc = buscarDocumento(residuoId, docId);
        return fileStorageService.resolve(doc.getCaminho());
    }

    // TODO: adicionar @PreAuthorize quando auth for implementado
    @Transactional
    public void deletarDocumento(Long residuoId, Long docId) {
        findById(residuoId);
        DocumentoResiduo doc = documentoRepository.findByIdAndResiduoId(docId, residuoId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));
        fileStorageService.delete(doc.getCaminho());
        documentoRepository.delete(doc);
    }

    public Specification<Residuo> buildSpec(CategoriaResiduo categoria, ClassePericulosidade classePericulosidade,
                                             TipoOferta tipoOferta, StatusResiduo status,
                                             BigDecimal precoMin, BigDecimal precoMax,
                                             Integer quantidadeMin, String search, Boolean aceiteTermos) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (aceiteTermos != null) predicates.add(cb.equal(root.get("aceiteTermos"), aceiteTermos));
            if (categoria != null) predicates.add(cb.equal(root.get("categoria"), categoria));
            if (classePericulosidade != null) predicates.add(cb.equal(root.get("classePericulosidade"), classePericulosidade));
            if (tipoOferta != null) predicates.add(cb.equal(root.get("tipoOferta"), tipoOferta));
            if (precoMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("preco"), precoMin));
            if (precoMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("preco"), precoMax));
            if (quantidadeMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("quantidadeKg"), quantidadeMin));
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nome")), pattern),
                        cb.like(cb.lower(root.get("localizacao")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void anexarDocumentos(Residuo residuo, MultipartFile laudoTecnico, MultipartFile orcamentoDescarte) {
        if (laudoTecnico != null && !laudoTecnico.isEmpty()) {
            String caminho = fileStorageService.store(laudoTecnico, residuo.getId());
            residuo.getDocumentos().add(DocumentoResiduo.builder()
                    .residuo(residuo)
                    .tipoDocumento(TipoDocumento.LAUDO_TECNICO)
                    .nomeArquivo(laudoTecnico.getOriginalFilename())
                    .caminho(caminho)
                    .contentType(laudoTecnico.getContentType())
                    .tamanhoBytes(laudoTecnico.getSize())
                    .build());
        }
        if (orcamentoDescarte != null && !orcamentoDescarte.isEmpty()) {
            String caminho = fileStorageService.store(orcamentoDescarte, residuo.getId());
            residuo.getDocumentos().add(DocumentoResiduo.builder()
                    .residuo(residuo)
                    .tipoDocumento(TipoDocumento.ORCAMENTO_DESCARTE)
                    .nomeArquivo(orcamentoDescarte.getOriginalFilename())
                    .caminho(caminho)
                    .contentType(orcamentoDescarte.getContentType())
                    .tamanhoBytes(orcamentoDescarte.getSize())
                    .build());
        }
    }

    Residuo findById(Long id) {
        return residuoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resíduo não encontrado com id: " + id));
    }

    private ResiduoResponseDTO toResponseDTO(Residuo r) {
        return ResiduoResponseDTO.builder()
                .id(r.getId())
                .nome(r.getNome())
                .categoria(r.getCategoria())
                .composicaoQuimica(r.getComposicaoQuimica())
                .classePericulosidade(r.getClassePericulosidade())
                .localizacao(r.getLocalizacao())
                .prazoDisponibilidade(r.getPrazoDisponibilidade())
                .tipoOferta(r.getTipoOferta())
                .preco(r.getPreco())
                .frete(r.getFrete())
                .quantidadeKg(r.getQuantidadeKg())
                .status(r.getStatus())
                .aceiteTermos(r.isAceiteTermos())
                .empresaId(r.getEmpresaId())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .documentos(r.getDocumentos().stream().map(this::toDocumentoDTO).collect(Collectors.toList()))
                .build();
    }

    private ResiduoSummaryDTO toSummaryDTO(Residuo r) {
        return ResiduoSummaryDTO.builder()
                .id(r.getId())
                .nome(r.getNome())
                .categoria(r.getCategoria())
                .classePericulosidade(r.getClassePericulosidade())
                .localizacao(r.getLocalizacao())
                .prazoDisponibilidade(r.getPrazoDisponibilidade())
                .tipoOferta(r.getTipoOferta())
                .preco(r.getPreco())
                .frete(r.getFrete())
                .quantidadeKg(r.getQuantidadeKg())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private ResiduoDetalheDTO toDetalheDTO(Residuo r) {
        return ResiduoDetalheDTO.builder()
                .id(r.getId())
                .nome(r.getNome())
                .categoria(r.getCategoria())
                .composicaoQuimica(r.getComposicaoQuimica())
                .classePericulosidade(r.getClassePericulosidade())
                .localizacao(r.getLocalizacao())
                .prazoDisponibilidade(r.getPrazoDisponibilidade())
                .tipoOferta(r.getTipoOferta())
                .preco(r.getPreco())
                .frete(r.getFrete())
                .quantidadeKg(r.getQuantidadeKg())
                .status(r.getStatus())
                .aceiteTermos(r.isAceiteTermos())
                .empresaId(r.getEmpresaId())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .documentos(r.getDocumentos().stream().map(this::toDocumentoDTO).collect(Collectors.toList()))
                .build();
    }

    private DocumentoResponseDTO toDocumentoDTO(DocumentoResiduo d) {
        return DocumentoResponseDTO.builder()
                .id(d.getId())
                .residuoId(d.getResiduo().getId())
                .tipoDocumento(d.getTipoDocumento())
                .nomeArquivo(d.getNomeArquivo())
                .contentType(d.getContentType())
                .tamanhoBytes(d.getTamanhoBytes())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
