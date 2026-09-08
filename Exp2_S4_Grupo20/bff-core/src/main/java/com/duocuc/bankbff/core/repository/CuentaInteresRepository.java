package com.duocuc.bankbff.core.repository;

import com.duocuc.bankbff.core.domain.entity.CuentaInteresEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaInteresRepository extends JpaRepository<CuentaInteresEntity, Long> {

    Optional<CuentaInteresEntity> findByCuentaOrigenId(Long cuentaOrigenId);

    Page<CuentaInteresEntity> findByTipo(String tipo, Pageable pageable);
}
