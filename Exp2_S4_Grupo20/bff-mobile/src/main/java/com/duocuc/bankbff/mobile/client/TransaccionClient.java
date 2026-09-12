package com.duocuc.bankbff.mobile.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TransaccionClient {

    private final RestClient transaccionesRestClient;

    public TransaccionClient(RestClient transaccionesRestClient) {
        this.transaccionesRestClient = transaccionesRestClient;
    }

    public List<TransaccionResponse> recientes() {
        return transaccionesRestClient.get()
                .uri("/transacciones/recientes")
                .retrieve()
                .body(new ParameterizedTypeReference<List<TransaccionResponse>>() {});
    }

    public record TransaccionResponse(Long transaccionOrigenId, LocalDate fecha, BigDecimal monto,
                                       String tipo, boolean anomalia, String motivoAnomalia) {
    }
}