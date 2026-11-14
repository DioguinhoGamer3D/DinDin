package com.diogo.dindin.controller;

import com.diogo.dindin.dto.TransacaoRequest;
import com.diogo.dindin.dto.TransacaoResponse;
import com.diogo.dindin.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final TransacaoService transacaoService;

    @GetMapping
    public List<TransacaoResponse> listar(
            @AuthenticationPrincipal UUID userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        return transacaoService.listar(userId, mes);
    }

    @PostMapping
    public ResponseEntity<TransacaoResponse> criar(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody TransacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transacaoService.criar(userId, request));
    }

    @PutMapping("/{id}")
    public TransacaoResponse atualizar(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody TransacaoRequest request) {
        return transacaoService.atualizar(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id) {
        transacaoService.deletar(userId, id);
        return ResponseEntity.noContent().build();
    }
}