package com.duocuc.bankbff.web.service;

import com.duocuc.bankbff.core.exception.CuentaNoEncontradaException;
import com.duocuc.bankbff.core.repository.CuentaAnualRepository;
import com.duocuc.bankbff.core.repository.CuentaInteresRepository;
import com.duocuc.bankbff.web.dto.CuentaAnualWebDto;
import com.duocuc.bankbff.web.dto.CuentaWebDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaWebService {

    private final CuentaInteresRepository cuentaRepository;
    private final CuentaAnualRepository cuentaAnualRepository;

    public Page<CuentaWebDto> listar(String tipo, Pageable pageable) {
        Page<com.duocuc.bankbff.core.domain.entity.CuentaInteresEntity> page = (tipo == null || tipo.isBlank())
                ? cuentaRepository.findAll(pageable)
                : cuentaRepository.findByTipo(tipo, pageable);
        return page.map(CuentaWebDto::from);
    }

    public CuentaWebDto obtener(Long cuentaOrigenId) {
        return cuentaRepository.findByCuentaOrigenId(cuentaOrigenId)
                .map(CuentaWebDto::from)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaOrigenId));
    }

    public List<CuentaAnualWebDto> historialAnual(Long cuentaOrigenId) {
        if (cuentaRepository.findByCuentaOrigenId(cuentaOrigenId).isEmpty()) {
            throw new CuentaNoEncontradaException(cuentaOrigenId);
        }
        return cuentaAnualRepository.findAllByCuentaOrigenIdOrderByFechaAsc(cuentaOrigenId)
                .stream().map(CuentaAnualWebDto::from).toList();
    }
}
