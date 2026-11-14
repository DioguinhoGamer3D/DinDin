package com.diogo.dindin.service;

import com.diogo.dindin.dto.CategoriaRequest;
import com.diogo.dindin.dto.CategoriaResponse;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.model.User;
import com.diogo.dindin.repository.CategoriaRepository;
import com.diogo.dindin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.diogo.dindin.exception.CategoriaEmUsoException;
import com.diogo.dindin.repository.TransacaoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private final UUID userId = UUID.randomUUID();

    // ---------- LISTAR ----------

    @Test
    void deveListarCategoriasDoUsuario() {
        Categoria categoria = criarCategoria("Alimentação", TipoTransacao.DESPESA);
        when(categoriaRepository.findByUserId(userId)).thenReturn(List.of(categoria));

        List<CategoriaResponse> resultado = categoriaService.listar(userId);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Alimentação");
        assertThat(resultado.get(0).tipo()).isEqualTo(TipoTransacao.DESPESA);
        assertThat(resultado.get(0).cor()).isEqualTo("#EF4444");
    }

    // ---------- CRIAR ----------

    @Test
    void deveCriarCategoriaVinculadaAoUsuarioDoToken() {
        User dono = User.builder().id(userId).build();
        when(userRepository.getReferenceById(userId)).thenReturn(dono);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria c = invocation.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CategoriaResponse resposta = categoriaService.criar(userId,
                new CategoriaRequest("Salário", TipoTransacao.RECEITA, "#10B981"));

        assertThat(resposta.id()).isNotNull();
        assertThat(resposta.nome()).isEqualTo("Salário");
        assertThat(resposta.cor()).isEqualTo("#10B981");

        verify(categoriaRepository).save(argThat(c ->
                c.getUser().getId().equals(userId)
                        && c.getTipo() == TipoTransacao.RECEITA
                        && c.getCor().equals("#10B981")));
    }


    // ---------- ATUALIZAR ----------

    @Test
    void deveAtualizarCategoriaDoUsuario() {
        Categoria existente = criarCategoria("Antigo", TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(transacaoRepository.existsByCategoriaId(existente.getId())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaResponse resposta = categoriaService.atualizar(userId, existente.getId(),
                new CategoriaRequest("Novo", TipoTransacao.RECEITA, "#3B82F6"));

        assertThat(resposta.nome()).isEqualTo("Novo");
        assertThat(resposta.tipo()).isEqualTo(TipoTransacao.RECEITA);
        assertThat(resposta.cor()).isEqualTo("#3B82F6");
    }

    @Test
    void naoDeveAtualizarCategoriaInexistenteOuDeOutroUsuario() {
        UUID id = UUID.randomUUID();
        when(categoriaRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.atualizar(userId, id,
                new CategoriaRequest("Qualquer", TipoTransacao.DESPESA, "#3B82F6")))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(categoriaRepository, never()).save(any());
    }

    // ---------- ALTERAR ----------

    @Test
    void naoDeveAlterarOTipoDeCategoriaEmUso() {
        Categoria existente = criarCategoria("Mercado", TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(transacaoRepository.existsByCategoriaId(existente.getId())).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.atualizar(userId, existente.getId(),
                new CategoriaRequest("Mercado", TipoTransacao.RECEITA, "#EF4444")))
                .isInstanceOf(CategoriaEmUsoException.class);

        verify(categoriaRepository, never()).save(any());
        assertThat(existente.getTipo()).isEqualTo(TipoTransacao.DESPESA);
    }

    @Test
    void deveAlterarNomeECorDeCategoriaEmUsoSemMudarOTipo() {
        Categoria existente = criarCategoria("Mercado", TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        // lenient: o teste falha se o service bloquear por engano, sem reclamar se ele nem consultar
        lenient().when(transacaoRepository.existsByCategoriaId(existente.getId())).thenReturn(true);
        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaResponse resposta = categoriaService.atualizar(userId, existente.getId(),
                new CategoriaRequest("Supermercado", TipoTransacao.DESPESA, "#3B82F6"));

        assertThat(resposta.nome()).isEqualTo("Supermercado");
        assertThat(resposta.cor()).isEqualTo("#3B82F6");
        assertThat(resposta.tipo()).isEqualTo(TipoTransacao.DESPESA);
    }

    // ---------- DELETAR ----------

    @Test
    void deveDeletarCategoriaDoUsuario() {
        Categoria existente = criarCategoria("Lazer", TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(transacaoRepository.existsByCategoriaId(existente.getId())).thenReturn(false);

        categoriaService.deletar(userId, existente.getId());

        verify(categoriaRepository).delete(existente);
    }

    @Test
    void naoDeveDeletarCategoriaInexistenteOuDeOutroUsuario() {
        UUID id = UUID.randomUUID();
        when(categoriaRepository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.deletar(userId, id))
                .isInstanceOf(RecursoNaoEncontradoException.class);

        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    void naoDeveDeletarCategoriaEmUso() {
        Categoria existente = criarCategoria("Mercado", TipoTransacao.DESPESA);
        when(categoriaRepository.findByIdAndUserId(existente.getId(), userId))
                .thenReturn(Optional.of(existente));
        when(transacaoRepository.existsByCategoriaId(existente.getId())).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.deletar(userId, existente.getId()))
                .isInstanceOf(CategoriaEmUsoException.class);

        verify(categoriaRepository, never()).delete(any());
    }

    private Categoria criarCategoria(String nome, TipoTransacao tipo) {
        return Categoria.builder()
                .id(UUID.randomUUID())
                .nome(nome)
                .tipo(tipo)
                .cor("#EF4444")
                .build();
    }
}