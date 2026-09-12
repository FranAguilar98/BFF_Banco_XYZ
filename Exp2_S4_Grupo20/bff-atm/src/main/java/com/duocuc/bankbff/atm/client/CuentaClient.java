package com.duocuc.bankbff.atm.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CuentaClient {

    private final RestClient cuentasRestClient;

    public CuentaResponse obtenerCuenta(Long cuentaOrigenId) {
        return cuentasRestClient.get()
                .uri("/cuentas/{id}", cuentaOrigenId)
                .retrieve()
                .body(CuentaResponse.class);
    }

    public CuentaResponse actualizarSaldo(Long cuentaOrigenId, BigDecimal nuevoSaldo) {
        return cuentasRestClient.patch()
                .uri("/cuentas/{id}/saldo", cuentaOrigenId)
                .body(new ActualizarSaldoRequest(nuevoSaldo))
                .retrieve()
                .body(CuentaResponse.class);
    }

    public record CuentaResponse(Long cuentaOrigenId, String nombre, String tipo,
                                  BigDecimal saldoFinal, BigDecimal tasaAplicada) {
    }

    private record ActualizarSaldoRequest(BigDecimal nuevoSaldo) {
    }
}