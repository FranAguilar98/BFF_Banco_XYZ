package com.duocuc.bankbff.web.dto;

import com.duocuc.bankbff.core.domain.entity.TransaccionEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransaccionWebDto(
        Long transaccionOrigenId,
        LocalDate fecha,
        BigDecimal monto,
        String tipo,
        boolean anomalia,
        String motivoAnomalia
) {
    public static TransaccionWebDto from(TransaccionEntity e) {
        return new TransaccionWebDto(
                e.getTransaccionOrigenId(), e.getFecha(), e.getMonto(),
                e.getTipo(), Boolean.TRUE.equals(e.getAnomalia()), e.getMotivoAnomalia()
        );
    }
}
