package com.nowaste.backend.service;

import com.nowaste.backend.domain.Residuo;
import com.nowaste.backend.domain.enums.*;
import com.nowaste.backend.dto.request.ResiduoRequestDTO;
import com.nowaste.backend.dto.response.ResiduoResponseDTO;
import com.nowaste.backend.exception.BadRequestException;
import com.nowaste.backend.exception.EntityNotFoundException;
import com.nowaste.backend.repository.DocumentoResiduoRepository;
import com.nowaste.backend.repository.ResiduoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResiduoServiceTest {

    @Mock
    private ResiduoRepository residuoRepository;

    @Mock
    private DocumentoResiduoRepository documentoRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ResiduoService residuoService;

    @Test
    void criar_comAceitouTermosFalse_deveLancarBadRequestException() {
        ResiduoRequestDTO dto = buildDto(false, TipoOferta.DOACAO, null);

        assertThatThrownBy(() -> residuoService.criar(dto, null, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("aceitouTermos");

        verifyNoInteractions(residuoRepository);
    }

    @Test
    void criar_comTipoOfertaVendaEPrecoNull_deveSalvarNormalmente() {
        ResiduoRequestDTO dto = buildDto(true, TipoOferta.VENDA, null);

        Residuo saved = Residuo.builder()
                .id(1L)
                .nome(dto.getNome())
                .categoria(dto.getCategoria())
                .classePericulosidade(dto.getClassePericulosidade())
                .localizacao(dto.getLocalizacao())
                .prazoDisponibilidade(dto.getPrazoDisponibilidade())
                .tipoOferta(dto.getTipoOferta())
                .preco(null)
                .frete(dto.getFrete())
                .quantidadeKg(dto.getQuantidadeKg())
                .aceiteTermos(true)
                .status(StatusResiduo.ATIVO)
                .build();

        when(residuoRepository.save(any(Residuo.class))).thenReturn(saved);

        ResiduoResponseDTO result = residuoService.criar(dto, null, null);

        assertThat(result).isNotNull();
        assertThat(result.getPreco()).isNull();
        assertThat(result.getTipoOferta()).isEqualTo(TipoOferta.VENDA);
        verify(residuoRepository, times(2)).save(any(Residuo.class));
    }

    @Test
    void buscarPorId_comIdInexistente_deveLancarEntityNotFoundException() {
        when(residuoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> residuoService.buscarPorId(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("999");
    }

    private ResiduoRequestDTO buildDto(boolean aceitouTermos, TipoOferta tipoOferta, BigDecimal preco) {
        ResiduoRequestDTO dto = new ResiduoRequestDTO();
        dto.setNome("Resíduo Teste");
        dto.setCategoria(CategoriaResiduo.SOLIDO);
        dto.setClassePericulosidade(ClassePericulosidade.CLASSE_IIB);
        dto.setLocalizacao("São Paulo, SP");
        dto.setPrazoDisponibilidade(LocalDate.now().plusMonths(3));
        dto.setTipoOferta(tipoOferta);
        dto.setPreco(preco);
        dto.setFrete(TipoFrete.NEGOCIAR);
        dto.setQuantidadeKg(100);
        dto.setAceitouTermos(aceitouTermos);
        return dto;
    }
}
