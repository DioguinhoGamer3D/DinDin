package com.diogo.dindin.controller;

import com.diogo.dindin.dto.CategoriaRequest;
import com.diogo.dindin.dto.CategoriaResponse;
import com.diogo.dindin.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponse> listar(@AuthenticationPrincipal UUID userId) {
        return categoriaService.listar(userId);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.criar(userId, request));
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request) {
        return categoriaService.atualizar(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID id) {
        categoriaService.deletar(userId, id);
        return ResponseEntity.noContent().build();
    }
}