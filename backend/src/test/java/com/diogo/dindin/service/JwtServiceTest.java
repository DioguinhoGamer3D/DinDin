package com.diogo.dindin.service;

import com.diogo.dindin.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "chave-secreta-de-teste-precisa-ter-pelo-menos-256-bits-para-hmac-sha";
    private static final long EXPIRATION_MS = 86400000; // 24h

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(SECRET, EXPIRATION_MS);
    }

    @Test
    void deveGerarTokenValido() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash")
                .build();

        String token = jwtService.gerarToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validarToken(token)).isTrue();
    }

    @Test
    void deveExtrairEmailCorretoDoToken() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash")
                .build();

        String token = jwtService.gerarToken(user);

        assertThat(jwtService.extrairEmail(token)).isEqualTo("diogo@teste.com");
    }

    @Test
    void naoDeveValidarTokenAdulterado() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash")
                .build();

        String token = jwtService.gerarToken(user);
        String tokenAdulterado = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.validarToken(tokenAdulterado)).isFalse();
    }

    @Test
    void naoDeveValidarTokenExpirado() throws InterruptedException {
        // Serviço com expiração quase instantânea pra forçar o cenário
        JwtService servicoComExpiracaoCurta = new JwtServiceImpl(SECRET, 1); // 1ms

        User user = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash")
                .build();

        String token = servicoComExpiracaoCurta.gerarToken(user);

        Thread.sleep(50); // garante que passou da expiração

        assertThat(servicoComExpiracaoCurta.validarToken(token)).isFalse();
    }

    @Test
    void naoDeveValidarTokenComAssinaturaDeOutraChave() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash")
                .build();

        String token = jwtService.gerarToken(user);

        JwtService servicoComOutraChave = new JwtServiceImpl(
                "outra-chave-secreta-completamente-diferente-tambem-com-256-bits", EXPIRATION_MS);

        assertThat(servicoComOutraChave.validarToken(token)).isFalse();
    }
}