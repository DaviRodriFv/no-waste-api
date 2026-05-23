package com.nowaste.backend.repository;

import com.nowaste.backend.domain.Residuo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResiduoRepository extends JpaRepository<Residuo, Long>, JpaSpecificationExecutor<Residuo> {
}
