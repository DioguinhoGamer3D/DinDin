package com.diogo.dindin.service;

import com.diogo.dindin.dto.CategoriaRequest;
import com.diogo.dindin.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    List<CategoriaResponse> listar(UUID userId);
    CategoriaResponse criar(UUID userId, CategoriaRequest request);
    CategoriaResponse atualizar(UUID userId, UUID id, CategoriaRequest request);
    void deletar(UUID userId, UUID id);
}