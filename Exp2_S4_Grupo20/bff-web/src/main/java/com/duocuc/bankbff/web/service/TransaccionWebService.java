package com.duocuc.bankbff.web.service;

import com.duocuc.bankbff.core.repository.TransaccionRepository;
import com.duocuc.bankbff.web.dto.TransaccionWebDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransaccionWebService {

    private final TransaccionRepository transaccionRepository;

    public Page<TransaccionWebDto> listar(LocalDate desde, LocalDate hasta, String tipo, Pageable pageable) {
        if (desde != null && hasta != null) {
            return transaccionRepository.findByFechaBetween(desde, hasta, pageable).map(TransaccionWebDto::from);
        }
        if (tipo != null && !tipo.isBlank()) {
            return transaccionRepository.findByTipo(tipo, pageable).map(TransaccionWebDto::from);
        }
        return transaccionRepository.findAll(pageable).map(TransaccionWebDto::from);
    }
}
