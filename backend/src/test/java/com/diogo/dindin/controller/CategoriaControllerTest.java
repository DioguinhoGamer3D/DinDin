package com.diogo.dindin.controller;

import com.diogo.dindin.config.SecurityConfig;
import com.diogo.dindin.dto.CategoriaRequest;
import com.diogo.dindin.dto.CategoriaResponse;
import com.diogo.dindin.exception.CategoriaEmUsoException;
import com.diogo.dindin.exception.RecursoNaoEncontradoException;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.security.JwtAuthenticationFilter;
import com.diogo.dindin.service.CategoriaService;
import com.diogo.dindin.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private JwtService jwtService;

    private final UUID userId = UUID.randomUUID();

    // Simula um usuário autenticado cujo principal é o UUID, como o filtro JWT faz
    private RequestPostProcessor logado() {
        return authentication(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
    }

    // ---------- SEGURANÇA ----------

    @Test
    void deveRejeitarRequisicaoSemAutenticacao() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(categoriaService);
    }

    // ---------- LISTAR ----------

    @Test
    void deveListarCategoriasDoUsuarioLogado() throws Exception {
        UUID id = UUID.randomUUID();
        when(categoriaService.listar(userId))
                .thenReturn(List.of(new CategoriaResponse(id, "Mercado", TipoTransacao.DESPESA)));

        mockMvc.perform(get("/categorias").with(logado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nome").value("Mercado"))
                .andExpect(jsonPath("$[0].tipo").value("DESPESA"));
    }

    // ---------- CRIAR ----------

    @Test
    void deveCriarCategoriaERetornar201() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaRequest request = new CategoriaRequest("Salario", TipoTransacao.RECEITA);
        when(categoriaService.criar(eq(userId), any(CategoriaRequest.class)))
                .thenReturn(new CategoriaResponse(id, "Salario", TipoTransacao.RECEITA));

        mockMvc.perform(post("/categorias")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Salario"));
    }

    @Test
    void deveRetornar400QuandoNomeVazio() throws Exception {
        CategoriaRequest request = new CategoriaRequest("", TipoTransacao.DESPESA);

        mockMvc.perform(post("/categorias")
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(categoriaService);
    }

    // ---------- ATUALIZAR ----------

    @Test
    void deveAtualizarCategoriaERetornar200() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaRequest request = new CategoriaRequest("Novo nome", TipoTransacao.DESPESA);
        when(categoriaService.atualizar(eq(userId), eq(id), any(CategoriaRequest.class)))
                .thenReturn(new CategoriaResponse(id, "Novo nome", TipoTransacao.DESPESA));

        mockMvc.perform(put("/categorias/{id}", id)
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Novo nome"));
    }

    @Test
    void deveRetornar404AoAtualizarCategoriaInexistenteOuDeOutroUsuario() throws Exception {
        UUID id = UUID.randomUUID();
        CategoriaRequest request = new CategoriaRequest("Qualquer", TipoTransacao.DESPESA);
        when(categoriaService.atualizar(eq(userId), eq(id), any(CategoriaRequest.class)))
                .thenThrow(new RecursoNaoEncontradoException("Categoria"));

        mockMvc.perform(put("/categorias/{id}", id)
                        .with(logado())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ---------- DELETAR ----------

    @Test
    void deveDeletarCategoriaERetornar204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/categorias/{id}", id).with(logado()))
                .andExpect(status().isNoContent());

        verify(categoriaService).deletar(userId, id);
    }

    @Test
    void deveRetornar409AoDeletarCategoriaEmUso() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CategoriaEmUsoException()).when(categoriaService).deletar(userId, id);

        mockMvc.perform(delete("/categorias/{id}", id).with(logado()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}