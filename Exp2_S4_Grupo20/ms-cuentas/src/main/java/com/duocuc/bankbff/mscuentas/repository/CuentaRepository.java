package com.duocuc.bankbff.mscuentas.repository;

import com.duocuc.bankbff.mscuentas.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByCuentaOrigenId(Long cuentaOrigenId);
}