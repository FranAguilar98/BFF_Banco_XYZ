package com.duocuc.bankbff.mobile.service;

import com.duocuc.bankbff.core.exception.CuentaNoEncontradaException;
import com.duocuc.bankbff.core.repository.CuentaInteresRepository;
import com.duocuc.bankbff.mobile.dto.CuentaResumenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CuentaMobileService {

    private final CuentaInteresRepository cuentaRepository;

    public CuentaResumenDto resumen(Long cuentaOrigenId) {
        return cuentaRepository.findByCuentaOrigenId(cuentaOrigenId)
                .map(CuentaResumenDto::from)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaOrigenId));
    }
}
