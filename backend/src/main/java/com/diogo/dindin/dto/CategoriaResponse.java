package com.diogo.dindin.dto;

import com.diogo.dindin.model.TipoTransacao;

import java.util.UUID;

public record CategoriaResponse(UUID id, String nome, TipoTransacao tipo) {
}