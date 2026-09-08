package com.duocuc.bankbff.core.repository;

import com.duocuc.bankbff.core.domain.entity.CuentaAnualEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuentaAnualRepository extends JpaRepository<CuentaAnualEntity, Long> {

    List<CuentaAnualEntity> findAllByCuentaOrigenIdOrderByFechaAsc(Long cuentaOrigenId);

    List<CuentaAnualEntity> findAllByOrderByCuentaOrigenIdAscFechaAsc();
}
