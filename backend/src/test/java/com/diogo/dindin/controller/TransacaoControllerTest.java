package com.diogo.dindin.controller;

import com.diogo.dindin.config.SecurityConfig;
import com.diogo.dindin.dto.TransacaoRequest;
import com.diogo.dindin.dto.TransacaoResponse;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.exception.TipoIncompativelException;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.security.JwtAuthenticationFilter;
import com.diogo.dindin.service.JwtService;
import com.diogo.dindin.service.TransacaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransacaoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransacaoService transacaoService;

    @MockitoBean
    private JwtService jwtService;

    private final UUID userId = UUID.randomUUID();
    private final UUID categoriaId = UUID.randomUUID();

    private RequestPostProcessor logado() {
        return authentication(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
    }

    // JSON escrito à mão: testa também o contrato da API (nomes dos campos e formato da data)
    private String corpo(String valor, String tipo) {
        return """
                {"descricao":"Mercado","valor":%s,"data":"2026-09-05","tipo":"%s","categoriaId":"%s"}
                """.formatted(valor, tipo, categoriaId);
    }

    private TransacaoResponse respostaPadrao(UUID id) {
        return new TransacaoResponse(id, "Mercado", new BigDecimal("89.90"),
                LocalDate.of(2026, 9, 5), TipoTransacao.DESPESA, categoriaId, "Alimentacao");
    }

    // ---------- SEGURANÇA ----------

    @Test
    void deveRejeitarRequisicaoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/transacoes").param("mes", "2026-09"))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(transacaoService);
    }

    // ---------- LISTAR ----------

    @Test
    void deveListarTransacoesDoMesInformado() throws Exception {
        UUID id = UUID.randomUUID();
        when(transacaoService.listar(userId, YearMonth.of(2026, 9)))
                .thenReturn(List.of(respostaPadrao(id)));

        mockMvc.perform(get("/transacoes").param("mes", "2026-09").with(logado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].descricao").value("Mercado"))
                .andExpect(jsonPath("$[0].tipo").value("DESPESA"))
                .andExpect(jsonPath("$[0].categoriaNome").value("Alimentacao"));
    }

    @Test
    void deveRetornar400QuandoMesNaoForInformado() throws Exception {
        mockMvc.perform(get("/transacoes").with(logado()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transacaoService);
    }

    @Test
    void deveRetornar400QuandoMesForInvalido() throws Exception {
        mockMvc.perform(get("/transacoes").param("mes", "setembro").with(logado()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transacaoService);
    }

    // ---------- CRIAR ----------

    @Test
    void deveCriarTransacaoERetornar201() throws Exception {
        UUID id = UUID.randomUUID();
        when(transacaoService.criar(eq(userId), any(TransacaoRequest.class)))
                .thenReturn(respostaPadrao(id));

        mockMvc.perform(post("/transacoes")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("89.90", "DESPESA")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.categoriaId").value(categoriaId.toString()));
    }

    @Test
    void deveRetornar400QuandoValorForNegativo() throws Exception {
        mockMvc.perform(post("/transacoes")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("-5", "DESPESA")))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transacaoService);
    }

    @Test
    void deveRetornar400QuandoTipoDiferenteDaCategoria() throws Exception {
        when(transacaoService.criar(eq(userId), any(TransacaoRequest.class)))
                .thenThrow(new TipoIncompativelException());

        mockMvc.perform(post("/transacoes")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("89.90", "RECEITA")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deveRetornar404QuandoCategoriaNaoForDoUsuario() throws Exception {
        when(transacaoService.criar(eq(userId), any(TransacaoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Categoria"));

        mockMvc.perform(post("/transacoes")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("89.90", "DESPESA")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---------- ATUALIZAR ----------

    @Test
    void deveAtualizarTransacaoERetornar200() throws Exception {
        UUID id = UUID.randomUUID();
        when(transacaoService.atualizar(eq(userId), eq(id), any(TransacaoRequest.class)))
                .thenReturn(respostaPadrao(id));

        mockMvc.perform(put("/transacoes/{id}", id)
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("89.90", "DESPESA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void deveRetornar404AoAtualizarTransacaoInexistenteOuDeOutroUsuario() throws Exception {
        UUID id = UUID.randomUUID();
        when(transacaoService.atualizar(eq(userId), eq(id), any(TransacaoRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Transação"));

        mockMvc.perform(put("/transacoes/{id}", id)
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo("89.90", "DESPESA")))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETAR ----------

    @Test
    void deveDeletarTransacaoERetornar204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/transacoes/{id}", id).with(logado()))
                .andExpect(status().isNoContent());

        verify(transacaoService).deletar(userId, id);
    }

    @Test
    void deveRetornar404AoDeletarTransacaoInexistenteOuDeOutroUsuario() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new RecursoNaoEncontradoException("Transação"))
                .when(transacaoService).deletar(userId, id);

        mockMvc.perform(delete("/transacoes/{id}", id).with(logado()))
                .andExpect(status().isNotFound());
    }
}