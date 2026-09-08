package com.duocuc.bankbff.mobile.service;

import com.duocuc.bankbff.core.repository.TransaccionRepository;
import com.duocuc.bankbff.mobile.dto.MovimientoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoMobileService {

    private final TransaccionRepository transaccionRepository;

    public List<MovimientoDto> recientes() {
        return transaccionRepository.findTop10ByOrderByFechaDescIdDesc()
                .stream().map(MovimientoDto::from).toList();
    }
}
