package com.diogo.dindin.dto;

import com.diogo.dindin.model.TipoTransacao;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransacaoRequest(
        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String descricao,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser maior que zero")
        @Digits(integer = 10, fraction = 2, message = "Valor deve ter no máximo 10 dígitos inteiros e 2 casas decimais")
        BigDecimal valor,

        @NotNull(message = "Data é obrigatória")
        LocalDate data,

        @NotNull(message = "Tipo é obrigatório")
        TipoTransacao tipo,

        @NotNull(message = "Categoria é obrigatória")
        UUID categoriaId
) {
}