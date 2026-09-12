package com.duocuc.bankbff.atm.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MovimientoClient {

    private final RestClient movimientosRestClient;

    public void registrarMovimiento(Long cuentaOrigenId, String transaccion, BigDecimal monto, String descripcion) {
        movimientosRestClient.post()
                .uri("/movimientos")
                .body(new NuevoMovimientoRequest(cuentaOrigenId, transaccion, monto, descripcion))
                .retrieve()
                .toBodilessEntity();
    }

    private record NuevoMovimientoRequest(Long cuentaOrigenId, String transaccion, BigDecimal monto, String descripcion) {
    }
}