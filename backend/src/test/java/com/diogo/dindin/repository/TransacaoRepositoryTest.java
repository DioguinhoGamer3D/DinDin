package com.diogo.dindin.repository;

import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.model.Transacao;
import com.diogo.dindin.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TransacaoRepositoryTest extends IntegrationTestBase {

    @Autowired private TransacaoRepository transacaoRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    @Test
    void deveFiltrarTransacoesPorMes() {
        User user = criarUsuario("user@teste.com");
        Categoria categoria = categoriaRepository.save(
                criarCategoria("Alimentação", user));

        transacaoRepository.save(criarTransacao(user, categoria, LocalDate.of(2026, 9, 5), new BigDecimal("100.00")));
        transacaoRepository.save(criarTransacao(user, categoria, LocalDate.of(2026, 9, 20), new BigDecimal("50.00")));
        transacaoRepository.save(criarTransacao(user, categoria, LocalDate.of(2026, 10, 1), new BigDecimal("30.00")));

        List<Transacao> setembro = transacaoRepository
                .findByUserIdAndDataBetween(user.getId(), LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertThat(setembro).hasSize(2);
    }

    private User criarUsuario(String email) {
        return userRepository.save(User.builder()
                .nome("Teste").email(email).senha("hash").build());
    }

    private Categoria criarCategoria(String nome, User dono) {
        return Categoria.builder()
                .nome(nome).tipo(TipoTransacao.DESPESA).user(dono).build();
    }

    private Transacao criarTransacao(User user, Categoria categoria, LocalDate data, BigDecimal valor) {
        return Transacao.builder()
                .descricao("teste")
                .valor(valor)
                .data(data)
                .tipo(TipoTransacao.DESPESA)
                .categoria(categoria)
                .user(user)
                .build();
    }
}
