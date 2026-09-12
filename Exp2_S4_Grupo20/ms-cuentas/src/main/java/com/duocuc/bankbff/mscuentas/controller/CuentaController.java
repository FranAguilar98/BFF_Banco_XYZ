package com.duocuc.bankbff.mscuentas.controller;

import com.duocuc.bankbff.mscuentas.dto.CuentaDTO;
import com.duocuc.bankbff.mscuentas.entity.CuentaEntity;
import com.duocuc.bankbff.mscuentas.exception.CuentaNoEncontradaException;
import com.duocuc.bankbff.mscuentas.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaRepository cuentaRepository;

    @GetMapping("/{cuentaOrigenId}")
    public CuentaDTO obtenerCuenta(@PathVariable Long cuentaOrigenId) {
        CuentaEntity cuenta = cuentaRepository.findByCuentaOrigenId(cuentaOrigenId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaOrigenId));
        return aDTO(cuenta);
    }

    private CuentaDTO aDTO(CuentaEntity entity) {
        return new CuentaDTO(
                entity.getCuentaOrigenId(), entity.getNombre(), entity.getTipo(),
                entity.getSaldoFinal(), entity.getTasaAplicada());
    }
}