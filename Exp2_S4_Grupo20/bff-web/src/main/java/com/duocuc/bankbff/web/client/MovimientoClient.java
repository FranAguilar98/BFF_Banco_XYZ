package com.duocuc.bankbff.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class MovimientoClient {

    private final RestClient movimientosRestClient;

    public MovimientoClient(RestClient movimientosRestClient) {
        this.movimientosRestClient = movimientosRestClient;
    }

    public List<MovimientoResponse> obtenerMovimientos(Long cuentaOrigenId) {
        return movimientosRestClient.get()
                .uri("/movimientos/cuenta/{id}", cuentaOrigenId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<MovimientoResponse>>() {});
    }

    public record MovimientoResponse(Long cuentaOrigenId, LocalDate fecha, String transaccion,
                                      BigDecimal monto, String descripcion) {
    }
}