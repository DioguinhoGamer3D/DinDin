package com.diogo.dindin.service;

import com.diogo.dindin.dto.TransacaoRequest;
import com.diogo.dindin.dto.TransacaoResponse;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface TransacaoService {
    List<TransacaoResponse> listar(UUID userId, YearMonth mes);
    TransacaoResponse criar(UUID userId, TransacaoRequest request);
    TransacaoResponse atualizar(UUID userId, UUID id, TransacaoRequest request);
    void deletar(UUID userId, UUID id);
}