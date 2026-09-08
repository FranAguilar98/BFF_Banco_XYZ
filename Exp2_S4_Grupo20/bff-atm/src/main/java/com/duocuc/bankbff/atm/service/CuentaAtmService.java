package com.duocuc.bankbff.atm.service;

import com.duocuc.bankbff.atm.dto.RetiroRequest;
import com.duocuc.bankbff.atm.dto.RetiroResponse;
import com.duocuc.bankbff.atm.dto.SaldoDto;
import com.duocuc.bankbff.core.domain.entity.CuentaInteresEntity;
import com.duocuc.bankbff.core.exception.CuentaNoEncontradaException;
import com.duocuc.bankbff.core.exception.SaldoInsuficienteException;
import com.duocuc.bankbff.core.repository.CuentaInteresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CuentaAtmService {

    private final CuentaInteresRepository cuentaRepository;

    public SaldoDto consultarSaldo(Long cuentaOrigenId) {
        CuentaInteresEntity cuenta = obtenerCuenta(cuentaOrigenId);
        return new SaldoDto(cuenta.getCuentaOrigenId(), cuenta.getSaldoFinal());
    }

    @Transactional
    public RetiroResponse retirar(Long cuentaOrigenId, RetiroRequest request) {
        CuentaInteresEntity cuenta = obtenerCuenta(cuentaOrigenId);

        if (cuenta.getSaldoFinal().compareTo(request.monto()) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigenId);
        }

        BigDecimal nuevoSaldo = cuenta.getSaldoFinal().subtract(request.monto());
        cuenta.setSaldoFinal(nuevoSaldo);
        cuentaRepository.save(cuenta);

        return new RetiroResponse(cuentaOrigenId, request.monto(), nuevoSaldo);
    }

    private CuentaInteresEntity obtenerCuenta(Long cuentaOrigenId) {
        return cuentaRepository.findByCuentaOrigenId(cuentaOrigenId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaOrigenId));
    }
}
