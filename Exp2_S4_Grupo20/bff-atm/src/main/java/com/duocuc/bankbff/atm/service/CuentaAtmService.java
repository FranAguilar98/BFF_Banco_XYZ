package com.duocuc.bankbff.atm.service;

import com.duocuc.bankbff.atm.client.CuentaClient;
import com.duocuc.bankbff.atm.client.MovimientoClient;
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
        CuentaClient.CuentaResponse cuenta = cuentaClient.obtenerCuenta(cuentaOrigenId);
        return new SaldoDto(cuenta.cuentaOrigenId(), cuenta.saldoFinal());
    }

    // Nota: ya no hay @Transactional real aqui - son dos llamadas HTTP a dos
    // microservicios distintos (ms-cuentas y ms-movimientos), asi que no hay
    // una unica base de datos que pueda revertir ambas si algo falla a mitad
    // de camino. Para el alcance de este proyecto se acepta la secuencia simple:
    // primero se baja el saldo, luego se registra el movimiento.
    public RetiroResponse retirar(Long cuentaOrigenId, RetiroRequest request) {
        CuentaClient.CuentaResponse cuenta = cuentaClient.obtenerCuenta(cuentaOrigenId);

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