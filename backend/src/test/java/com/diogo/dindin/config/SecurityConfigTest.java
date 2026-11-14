package com.diogo.dindin.config;

import com.diogo.dindin.controller.CategoriaController;
import com.diogo.dindin.security.JwtAuthenticationFilter;
import com.diogo.dindin.service.CategoriaService;
import com.diogo.dindin.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriaController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService categoriaService;

    @MockitoBean
    private JwtService jwtService;

    // ---------- CORS ----------

    @Test
    void devePermitirPreflightDoFrontendEmDesenvolvimento() throws Exception {
        mockMvc.perform(options("/categorias")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void deveBloquearPreflightDeOrigemNaoPermitida() throws Exception {
        mockMvc.perform(options("/categorias")
                        .header("Origin", "http://site-malicioso.com")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }

    // ---------- 401 ----------

    @Test
    void deveDevolver401ParaRequisicaoSemToken() throws Exception {
        mockMvc.perform(get("/categorias"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveDevolver401ParaTokenInvalido() throws Exception {
        mockMvc.perform(get("/categorias").header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }
}