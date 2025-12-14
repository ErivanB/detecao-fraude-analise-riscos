package com.projeto.detectorFraudes.dto;

public record AlertaFinanceiroDTO(
        String origem,
        String destino,
        double valor,
        String motivo
) {}