package com.duocuc.bankbff.atm.service;

import com.duocuc.bankbff.core.client.CuentaClient;
import com.duocuc.bankbff.core.client.MovimientoClient;
import com.duocuc.bankbff.atm.dto.RetiroRequest;
import com.duocuc.bankbff.atm.dto.RetiroResponse;
import com.duocuc.bankbff.atm.dto.SaldoDto;
import com.duocuc.bankbff.core.exception.SaldoInsuficienteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CuentaAtmService {

    private final CuentaClient cuentaClient;
    private final MovimientoClient movimientoClient;

    public SaldoDto consultarSaldo(Long cuentaOrigenId) {
        CuentaClient.CuentaResponse cuenta = cuentaClient.obtener(cuentaOrigenId);
        return new SaldoDto(cuenta.cuentaOrigenId(), cuenta.saldoFinal());
    }

    public RetiroResponse retirar(Long cuentaOrigenId, RetiroRequest request) {
        CuentaClient.CuentaResponse cuenta = cuentaClient.obtener(cuentaOrigenId);

        if (cuenta.saldoFinal().compareTo(request.monto()) < 0) {
            throw new SaldoInsuficienteException(cuentaOrigenId);
        }

        BigDecimal nuevoSaldo = cuenta.saldoFinal().subtract(request.monto());
        cuentaClient.actualizarSaldo(cuentaOrigenId, nuevoSaldo);

        movimientoClient.registrarMovimiento(
                cuentaOrigenId, "retiro", request.monto(), "Retiro cajero automatico");

        return new RetiroResponse(cuentaOrigenId, request.monto(), nuevoSaldo);
    }
}