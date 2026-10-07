package com.diogo.dindin.service;

import com.diogo.dindin.dto.TransacaoRequest;
import com.diogo.dindin.dto.TransacaoResponse;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.exception.TipoIncompativelException;
import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.model.Transacao;
import com.diogo.dindin.model.User;
import com.diogo.dindin.repository.CategoriaRepository;
import com.diogo.dindin.repository.TransacaoRepository;
import com.diogo.dindin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransacaoServiceImpl transacaoService;

    private final UUID userId = UUID.randomUUID();

    // ---------- LISTAR ----------

    @Test
    void deveListarTransacoesDoMesComAsMaisRecentesPrimeiro() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        Transacao antiga = criarTransacao(categoria, LocalDate.of(2026, 9, 5), "100.00");
        Transacao recente = criarTransacao(categoria, LocalDate.of(2026, 9, 20), "50.00");

        when(transacaoRepository.findByUserIdAndDataBetween(
                userId, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30)))
                .thenReturn(List.of(antiga, recente));

        List<TransacaoResponse> resultado = transacaoService.listar(userId, YearMonth.of(2026, 9));

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).data()).isEqualTo(LocalDate.of(2026, 9, 20));
        assertThat(resultado.get(1).data()).isEqualTo(LocalDate.of(2026, 9, 5));
    }

    // ---------- CRIAR ----------

    @Test
    void deveCriarTransacaoVinculadaAoUsuarioEACategoria() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        User dono = User.builder().id(userId).build();
        when(categoriaRepository.findByIdAndUserId(categoria.getId(), userId))
                .thenReturn(Optional.of(categoria));
        when(userRepository.getReferenceById(userId)).thenReturn(dono);
        when(transacaoRepository.save(any(Transacao.class))).thenAnswer(invocation -> {
            Transacao t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        TransacaoResponse resposta = transacaoService.criar(userId, criarRequest(categoria, TipoTransacao.DESPESA));

        assertThat(resposta.id()).isNotNull();
        assertThat(resposta.categoriaId()).isEqualTo(categoria.getId());
        assertThat(resposta.categoriaNome()).isEqualTo("Mercado");

        verify(transacaoRepository).save(argThat(t ->
                t.getUser().getId().equals(userId) && t.getCategoria() == categoria));
    }

    @Test
    void naoDeveCriarComCategoriaInexistenteOuDeOutroUsuario() {
        UUID categoriaId = UUID.randomUUID();
        when(categoriaRepository.findByIdAndUserId(categoriaId, userId)).thenReturn(Optional.empty());

        TransacaoRequest request = new TransacaoRequest(
                "Mercado", new BigDecimal("10.00"), LocalDate.of(2026, 9, 5),
                TipoTransacao.DESPESA, categoriaId);

        assertThatThrownBy(() -> transacaoService.criar(userId, request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarQuandoTipoDiferenteDoTipoDaCategoria() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(categoria.getId(), userId))
                .thenReturn(Optional.of(categoria));

        TransacaoRequest request = criarRequest(categoria, TipoTransacao.RECEITA);

        assertThatThrownBy(() -> transacaoService.criar(userId, request))
                .isInstanceOf(TipoIncompativelException.class);

        verify(transacaoRepository, never()).save(any());
    }

    // ---------- ATUALIZAR ----------

    @Test
    void deveAtualizarTransacaoDoUsuario() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        Transacao existente = criarTransacao(categoria, LocalDate.of(2026, 9, 5), "10.00");
        when(transacaoRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(categoriaRepository.findByIdAndUserId(categoria.getId(), userId))
                .thenReturn(Optional.of(categoria));
        when(transacaoRepository.save(any(Transacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TransacaoRequest request = new TransacaoRequest(
                "Feira", new BigDecimal("45.50"), LocalDate.of(2026, 9, 12),
                TipoTransacao.DESPESA, categoria.getId());

        TransacaoResponse resposta = transacaoService.atualizar(userId, existente.getId(), request);

        assertThat(resposta.descricao()).isEqualTo("Feira");
        assertThat(resposta.valor()).isEqualByComparingTo("45.50");
        assertThat(resposta.data()).isEqualTo(LocalDate.of(2026, 9, 12));
    }

    @Test
    void naoDeveAtualizarTransacaoInexistenteOuDeOutroUsuario() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        UUID id = UUID.randomUUID();
        when(transacaoRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService.atualizar(userId, id,
                criarRequest(categoria, TipoTransacao.DESPESA)))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarParaCategoriaDeOutroUsuario() {
        Categoria categoriaAtual = criarCategoria(TipoTransacao.DESPESA);
        Transacao existente = criarTransacao(categoriaAtual, LocalDate.of(2026, 9, 5), "10.00");
        UUID categoriaAlheia = UUID.randomUUID();
        when(transacaoRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(categoriaRepository.findByIdAndUserId(categoriaAlheia, userId))
                .thenReturn(Optional.empty());

        TransacaoRequest request = new TransacaoRequest(
                "Qualquer", new BigDecimal("10.00"), LocalDate.of(2026, 9, 5),
                TipoTransacao.DESPESA, categoriaAlheia);

        assertThatThrownBy(() -> transacaoService.atualizar(userId, existente.getId(), request))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(transacaoRepository, never()).save(any());
    }

    // ---------- DELETAR ----------

    @Test
    void deveDeletarTransacaoDoUsuario() {
        Categoria categoria = criarCategoria(TipoTransacao.DESPESA);
        Transacao existente = criarTransacao(categoria, LocalDate.of(2026, 9, 5), "10.00");
        when(transacaoRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));

        transacaoService.deletar(userId, existente.getId());

        verify(transacaoRepository).delete(existente);
    }

    @Test
    void naoDeveDeletarTransacaoInexistenteOuDeOutroUsuario() {
        UUID id = UUID.randomUUID();
        when(transacaoRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transacaoService.deletar(userId, id))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(transacaoRepository, never()).delete(any());
    }

    // ---------- helpers ----------

    private Categoria criarCategoria(TipoTransacao tipo) {
        return Categoria.builder()
                .id(UUID.randomUUID())
                .nome("Mercado")
                .tipo(tipo)
                .build();
    }

    private Transacao criarTransacao(Categoria categoria, LocalDate data, String valor) {
        return Transacao.builder()
                .id(UUID.randomUUID())
                .descricao("teste")
                .valor(new BigDecimal(valor))
                .data(data)
                .tipo(categoria.getTipo())
                .categoria(categoria)
                .build();
    }

    private TransacaoRequest criarRequest(Categoria categoria, TipoTransacao tipo) {
        return new TransacaoRequest(
                "Compras", new BigDecimal("89.90"), LocalDate.of(2026, 9, 5),
                tipo, categoria.getId());
    }
}