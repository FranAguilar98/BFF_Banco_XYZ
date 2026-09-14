package com.duocuc.bankbff.mobile.service;

import com.duocuc.bankbff.core.client.TransaccionClient;
import com.duocuc.bankbff.mobile.dto.MovimientoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoMobileService {

    private final TransaccionClient transaccionClient;

    public List<MovimientoDto> recientes() {
        return transaccionClient.recientes()
                .stream().map(MovimientoDto::from).toList();
    }
}