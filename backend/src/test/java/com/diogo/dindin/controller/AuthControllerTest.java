package com.diogo.dindin.controller;

import com.diogo.dindin.config.SecurityConfig;
import com.diogo.dindin.dto.AuthResponse;
import com.diogo.dindin.dto.LoginRequest;
import com.diogo.dindin.dto.RegisterRequest;
import com.diogo.dindin.exception.CredenciaisInvalidasException;
import com.diogo.dindin.exception.EmailJaCadastradoException;
import com.diogo.dindin.security.JwtAuthenticationFilter;
import com.diogo.dindin.service.AuthService;
import com.diogo.dindin.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    // ---------- REGISTER ----------

    @Test
    void deveRegistrarERetornar201ComToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "Senha123");
        when(authService.registrar(any())).thenReturn(new AuthResponse("token-fake"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-fake"));
    }

    @Test
    void deveRetornar409QuandoEmailJaCadastrado() throws Exception {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "Senha123");
        when(authService.registrar(any())).thenThrow(new EmailJaCadastradoException("diogo@teste.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar400QuandoSenhaForaDoPadrao() throws Exception {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoEmailForaDoFormato() throws Exception {
        RegisterRequest request = new RegisterRequest("Diogo", "nao-e-email", "Senha123");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ---------- LOGIN ----------

    @Test
    void deveLogarERetornar200ComToken() throws Exception {
        LoginRequest request = new LoginRequest("diogo@teste.com", "Senha123");
        when(authService.login(any())).thenReturn(new AuthResponse("token-fake"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-fake"));
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("diogo@teste.com", "senhaErrada");
        when(authService.login(any())).thenThrow(new CredenciaisInvalidasException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    // ---------- ROTA PÚBLICA x PROTEGIDA ----------

    @Test
    void rotaDeAuthDeveSerAcessivelSemToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "Senha123");
        when(authService.registrar(any())).thenReturn(new AuthResponse("token-fake"));

        // Sem header Authorization algum — e mesmo assim deve passar,
        // confirmando que /auth/** está liberado no SecurityFilterChain
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}