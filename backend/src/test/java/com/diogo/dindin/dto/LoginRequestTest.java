package com.diogo.dindin.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void deveAceitarDadosValidos() {
        LoginRequest request = new LoginRequest("diogo@teste.com", "qualquerSenha123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isEmpty();
    }

    @Test
    void deveRejeitarEmailComFormatoInvalido() {
        LoginRequest request = new LoginRequest("nao-e-um-email", "qualquerSenha123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    void deveRejeitarEmailVazio() {
        LoginRequest request = new LoginRequest("", "qualquerSenha123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    void deveRejeitarSenhaVazia() {
        LoginRequest request = new LoginRequest("diogo@teste.com", "");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    void deveAceitarSenhaQueNaoSegueOPadraoDeCadastro() {
        // Login não valida formato de senha (maiúscula/número/8 chars) —
        // só cadastro exige isso. Isso evita travar login de usuários
        // antigos se a política de senha mudar no futuro.
        LoginRequest request = new LoginRequest("diogo@teste.com", "123");

        Set<ConstraintViolation<LoginRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isEmpty();
    }
}