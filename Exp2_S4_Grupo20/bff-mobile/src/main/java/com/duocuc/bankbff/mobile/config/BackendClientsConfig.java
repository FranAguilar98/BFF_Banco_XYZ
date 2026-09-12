package com.duocuc.bankbff.mobile.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class BackendClientsConfig {

    @Bean
    public ClientHttpRequestFactory backendRequestFactory(
            @Value("${backend.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${backend.read-timeout-ms:3000}") int readTimeoutMs) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        return factory;
    }

    @Bean
    public RestClient cuentasRestClient(@Value("${backend.ms-cuentas.base-url}") String baseUrl,
                                         ClientHttpRequestFactory backendRequestFactory) {
        return RestClient.builder().baseUrl(baseUrl).requestFactory(backendRequestFactory).build();
    }

    @Bean
    public RestClient transaccionesRestClient(@Value("${backend.ms-transacciones.base-url}") String baseUrl,
                                                ClientHttpRequestFactory backendRequestFactory) {
        return RestClient.builder().baseUrl(baseUrl).requestFactory(backendRequestFactory).build();
    }
}