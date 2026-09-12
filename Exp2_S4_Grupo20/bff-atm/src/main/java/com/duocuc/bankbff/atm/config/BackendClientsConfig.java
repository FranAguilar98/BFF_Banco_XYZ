package com.duocuc.bankbff.atm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BackendClientsConfig {

    @Bean
    public RestClient cuentasRestClient(@Value("${backend.ms-cuentas.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient movimientosRestClient(@Value("${backend.ms-movimientos.base-url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}