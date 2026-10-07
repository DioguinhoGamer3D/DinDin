package com.diogo.dindin.repository;

import com.diogo.dindin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
    @EntityGraph(attributePaths = "categoria")
    List<Transacao> findByUserIdAndDataBetween(UUID userId, LocalDate inicio, LocalDate fim);
    boolean existsByCategoriaId(UUID categoriaId);
    Optional<Transacao> findByIdAndUserId(UUID id, UUID userId);
}