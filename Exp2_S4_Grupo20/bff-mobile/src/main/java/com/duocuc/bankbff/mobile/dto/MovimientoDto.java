package com.duocuc.bankbff.mobile.dto;

import com.duocuc.bankbff.core.domain.entity.TransaccionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimientoDto(LocalDate fecha, BigDecimal monto, String tipo) {
    public static MovimientoDto from(TransaccionEntity e) {
        return new MovimientoDto(e.getFecha(), e.getMonto(), e.getTipo());
    }
}
