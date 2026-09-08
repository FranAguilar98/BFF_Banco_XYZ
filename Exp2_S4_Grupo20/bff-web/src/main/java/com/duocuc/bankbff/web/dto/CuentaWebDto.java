package com.duocuc.bankbff.web.dto;

import com.duocuc.bankbff.core.domain.entity.CuentaInteresEntity;

import java.math.BigDecimal;


public record CuentaWebDto(
        Long cuentaOrigenId,
        String nombre,
        String tipo,
        Integer edad,
        BigDecimal saldoInicial,
        BigDecimal tasaAplicada,
        BigDecimal interesCalculado,
        BigDecimal saldoFinal
) {
    public static CuentaWebDto from(CuentaInteresEntity e) {
        return new CuentaWebDto(
                e.getCuentaOrigenId(), e.getNombre(), e.getTipo(), e.getEdad(),
                e.getSaldoInicial(), e.getTasaAplicada(), e.getInteresCalculado(), e.getSaldoFinal()
        );
    }
}
