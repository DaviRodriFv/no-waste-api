package com.nowaste.backend.dto.response;

import com.nowaste.backend.domain.enums.TipoDocumento;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class DocumentoResponseDTO {
    private Long id;
    private Long residuoId;
    private TipoDocumento tipoDocumento;
    private String nomeArquivo;
    private String contentType;
    private Long tamanhoBytes;
    private OffsetDateTime createdAt;
}
