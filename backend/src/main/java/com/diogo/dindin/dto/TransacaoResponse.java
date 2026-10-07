package com.diogo.dindin.dto;

import com.diogo.dindin.model.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoResponse(
        UUID id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        TipoTransacao tipo,
        UUID categoriaId,
        String categoriaNome
) {
}