package com.duocuc.bankbff.web.service;

import com.duocuc.bankbff.core.client.CuentaClient;
import com.duocuc.bankbff.core.client.MovimientoClient;
import com.duocuc.bankbff.web.dto.CuentaAnualWebDto;
import com.duocuc.bankbff.web.dto.CuentaWebDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaWebService {

    private final CuentaClient cuentaClient;
    private final MovimientoClient movimientoClient;

    public Page<CuentaWebDto> listar(String tipo, Pageable pageable) {
        CuentaClient.PageResponse<CuentaClient.CuentaResponse> page = cuentaClient.listar(tipo, pageable);
        List<CuentaWebDto> contenido = page.content().stream().map(CuentaWebDto::from).toList();
        return new PageImpl<>(contenido, pageable, page.totalElements());
    }

    public CuentaWebDto obtener(Long cuentaOrigenId) {
        return CuentaWebDto.from(cuentaClient.obtener(cuentaOrigenId));
    }

    public List<CuentaAnualWebDto> historialAnual(Long cuentaOrigenId) {
        return movimientoClient.obtenerMovimientos(cuentaOrigenId)
                .stream().map(CuentaAnualWebDto::from).toList();
    }
}