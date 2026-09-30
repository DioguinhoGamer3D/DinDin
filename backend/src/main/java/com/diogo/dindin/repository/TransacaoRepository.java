package com.diogo.dindin.repository;

import com.diogo.dindin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {
    List<Transacao> findByUserIdAndDataBetween(UUID userId, LocalDate inicio, LocalDate fim);
}