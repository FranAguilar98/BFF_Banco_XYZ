package com.duocuc.bankbff.web.service;

import com.duocuc.bankbff.core.client.TransaccionClient;
import com.duocuc.bankbff.core.client.CuentaClient;
import com.duocuc.bankbff.web.dto.TransaccionWebDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionWebService {

    private final TransaccionClient transaccionClient;

    public Page<TransaccionWebDto> listar(LocalDate desde, LocalDate hasta, String tipo, Pageable pageable) {
        CuentaClient.PageResponse<TransaccionClient.TransaccionResponse> page =
                transaccionClient.listar(desde, hasta, tipo, pageable);
        List<TransaccionWebDto> contenido = page.content().stream().map(TransaccionWebDto::from).toList();
        return new PageImpl<>(contenido, pageable, page.totalElements());
    }
}