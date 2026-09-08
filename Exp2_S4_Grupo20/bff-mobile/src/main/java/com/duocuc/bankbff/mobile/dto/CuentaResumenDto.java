package com.duocuc.bankbff.mobile.dto;

import com.duocuc.bankbff.core.domain.entity.CuentaInteresEntity;

import java.math.BigDecimal;

public record CuentaResumenDto(Long cuentaOrigenId, String nombre, String tipo, BigDecimal saldo) {
    public static CuentaResumenDto from(CuentaInteresEntity e) {
        return new CuentaResumenDto(e.getCuentaOrigenId(), e.getNombre(), e.getTipo(), e.getSaldoFinal());
    }
}
