package com.duocuc.bankbff.mobile.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class CuentaClient {

    private final RestClient cuentasRestClient;

    public CuentaClient(RestClient cuentasRestClient) {
        this.cuentasRestClient = cuentasRestClient;
    }

    public CuentaResponse obtener(Long cuentaOrigenId) {
        return cuentasRestClient.get()
                .uri("/cuentas/{id}", cuentaOrigenId)
                .retrieve()
                .body(CuentaResponse.class);
    }

    public record CuentaResponse(Long cuentaOrigenId, String nombre, String tipo, Integer edad,
                                  BigDecimal saldoInicial, BigDecimal tasaAplicada,
                                  BigDecimal interesCalculado, BigDecimal saldoFinal) {
    }
}