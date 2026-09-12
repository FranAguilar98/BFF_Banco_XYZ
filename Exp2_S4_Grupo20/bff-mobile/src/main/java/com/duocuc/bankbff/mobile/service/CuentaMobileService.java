package com.duocuc.bankbff.mobile.service;

import com.duocuc.bankbff.mobile.client.CuentaClient;
import com.duocuc.bankbff.mobile.dto.CuentaResumenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaMobileService {

    private final CuentaClient cuentaClient;

    public CuentaResumenDto resumen(Long cuentaOrigenId) {
        return CuentaResumenDto.from(cuentaClient.obtener(cuentaOrigenId));
    }
}