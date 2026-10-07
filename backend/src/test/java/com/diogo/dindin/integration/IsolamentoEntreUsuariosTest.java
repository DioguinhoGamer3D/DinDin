package com.diogo.dindin.integration;

import com.diogo.dindin.repository.IntegrationTestBase;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class IsolamentoEntreUsuariosTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    private String tokenA;
    private String tokenB;

    @BeforeEach
    void criarUsuarios() throws Exception {
        tokenA = registrar("A");
        tokenB = registrar("B");
    }

    // ---------- SEM AUTENTICAÇÃO ----------

    @Test
    void deveRejeitarRequisicaoSemTokenOuComTokenInvalido() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/categorias").header(AUTHORIZATION, "Bearer token-invalido"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/transacoes").param("mes", "2026-09"))
                .andExpect(status().is4xxClientError());
    }

    // ---------- CATEGORIAS ----------

    @Test
    void usuarioNaoDeveVerCategoriasDeOutroUsuario() throws Exception {
        criarCategoria(tokenA, "Alimentacao", "DESPESA");

        mockMvc.perform(get("/categorias").header(AUTHORIZATION, bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/categorias").header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void usuarioNaoDeveEditarCategoriaDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");

        mockMvc.perform(put("/categorias/{id}", categoriaA)
                        .header(AUTHORIZATION, bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Alterada\",\"tipo\":\"DESPESA\"}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/categorias").header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(jsonPath("$[0].nome").value("Alimentacao"));
    }

    @Test
    void usuarioNaoDeveApagarCategoriaDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");

        mockMvc.perform(delete("/categorias/{id}", categoriaA)
                        .header(AUTHORIZATION, bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/categorias").header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void naoDeveApagarCategoriaEmUso() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");
        criarTransacao(tokenA, categoriaA, "DESPESA");

        mockMvc.perform(delete("/categorias/{id}", categoriaA)
                        .header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(status().isConflict());
    }

    // ---------- TRANSAÇÕES ----------

    @Test
    void usuarioNaoDeveVerTransacoesDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");
        criarTransacao(tokenA, categoriaA, "DESPESA");

        mockMvc.perform(get("/transacoes").param("mes", "2026-09")
                        .header(AUTHORIZATION, bearer(tokenB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(get("/transacoes").param("mes", "2026-09")
                        .header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void usuarioNaoDeveEditarTransacaoDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");
        String transacaoA = criarTransacao(tokenA, categoriaA, "DESPESA");
        String categoriaB = criarCategoria(tokenB, "Lazer", "DESPESA");

        // B usa a PRÓPRIA categoria, então o 404 vem da transação ser de A
        String corpo = """
                {"descricao":"Alterada","valor":1.00,"data":"2026-09-05","tipo":"DESPESA","categoriaId":"%s"}
                """.formatted(categoriaB);

        mockMvc.perform(put("/transacoes/{id}", transacaoA)
                        .header(AUTHORIZATION, bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/transacoes").param("mes", "2026-09")
                        .header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(jsonPath("$[0].descricao").value("Mercado"));
    }

    @Test
    void usuarioNaoDeveApagarTransacaoDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");
        String transacaoA = criarTransacao(tokenA, categoriaA, "DESPESA");

        mockMvc.perform(delete("/transacoes/{id}", transacaoA)
                        .header(AUTHORIZATION, bearer(tokenB)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/transacoes").param("mes", "2026-09")
                        .header(AUTHORIZATION, bearer(tokenA)))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void usuarioNaoDeveCriarTransacaoComCategoriaDeOutroUsuario() throws Exception {
        String categoriaA = criarCategoria(tokenA, "Alimentacao", "DESPESA");

        String corpo = """
                {"descricao":"Intruso","valor":10.00,"data":"2026-09-05","tipo":"DESPESA","categoriaId":"%s"}
                """.formatted(categoriaA);

        mockMvc.perform(post("/transacoes")
                        .header(AUTHORIZATION, bearer(tokenB))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isNotFound());
    }

    // ---------- helpers ----------

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String registrar(String nome) throws Exception {
        String email = nome.toLowerCase() + "-" + UUID.randomUUID() + "@teste.com";
        String corpo = """
                {"nome":"%s","email":"%s","senha":"Senha123"}
                """.formatted(nome, email);

        String resposta = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.token");
    }

    private String criarCategoria(String token, String nome, String tipo) throws Exception {
        String corpo = """
                {"nome":"%s","tipo":"%s"}
                """.formatted(nome, tipo);

        String resposta = mockMvc.perform(post("/categorias")
                        .header(AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }

    private String criarTransacao(String token, String categoriaId, String tipo) throws Exception {
        String corpo = """
                {"descricao":"Mercado","valor":89.90,"data":"2026-09-05","tipo":"%s","categoriaId":"%s"}
                """.formatted(tipo, categoriaId);

        String resposta = mockMvc.perform(post("/transacoes")
                        .header(AUTHORIZATION, bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(resposta, "$.id");
    }
}