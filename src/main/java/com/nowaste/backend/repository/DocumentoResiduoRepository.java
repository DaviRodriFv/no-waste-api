package com.nowaste.backend.repository;

import com.nowaste.backend.domain.DocumentoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoResiduoRepository extends JpaRepository<DocumentoResiduo, Long> {
    List<DocumentoResiduo> findByResiduoId(Long residuoId);
    Optional<DocumentoResiduo> findByIdAndResiduoId(Long id, Long residuoId);
}
