package com.duocuc.bankbff.mscuentas.dto;

import java.math.BigDecimal;

public record CuentaDTO(
        Long cuentaOrigenId,
        String nombre,
        String tipo,
        BigDecimal saldoFinal,
        BigDecimal tasaAplicada) {
}