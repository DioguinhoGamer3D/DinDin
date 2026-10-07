package com.diogo.dindin.dto;

import com.diogo.dindin.model.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "Tipo é obrigatório")
        TipoTransacao tipo
) {
}