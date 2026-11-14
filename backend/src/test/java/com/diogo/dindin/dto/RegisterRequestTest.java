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

class RegisterRequestTest {

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
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "Senha123");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isEmpty();
    }

    @Test
    void deveRejeitarSenhaComMenosDe8Caracteres() {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "Abc123");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("senha"));
    }

    @Test
    void deveRejeitarSenhaSemLetraMaiuscula() {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "senha123");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    void deveRejeitarSenhaSemNumero() {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "SenhaSemNumero");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }

    @Test
    void deveRejeitarEmailComFormatoInvalido() {
        RegisterRequest request = new RegisterRequest("Diogo", "nao-e-um-email", "Senha123");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes)
                .extracting(ConstraintViolation::getPropertyPath)
                .anyMatch(path -> path.toString().equals("email"));
    }

    @Test
    void deveRejeitarNomeVazio() {
        RegisterRequest request = new RegisterRequest("", "diogo@teste.com", "Senha123");

        Set<ConstraintViolation<RegisterRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
    }
}