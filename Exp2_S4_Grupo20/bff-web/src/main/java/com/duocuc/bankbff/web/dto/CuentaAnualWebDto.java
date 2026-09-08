package com.duocuc.bankbff.web.dto;

import com.duocuc.bankbff.core.domain.entity.CuentaAnualEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaAnualWebDto(
        LocalDate fecha,
        String transaccion,
        BigDecimal monto,
        String descripcion
) {
    public static CuentaAnualWebDto from(CuentaAnualEntity e) {
        return new CuentaAnualWebDto(e.getFecha(), e.getTransaccion(), e.getMonto(), e.getDescripcion());
    }
}
