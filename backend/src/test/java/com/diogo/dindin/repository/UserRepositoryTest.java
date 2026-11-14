package com.diogo.dindin.repository;

import com.diogo.dindin.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserRepositoryTest extends IntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void deveSalvarEBuscarUsuarioPorEmail() {
        User user = User.builder()
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash-fake")
                .build();

        userRepository.save(user);

        Optional<User> encontrado = userRepository.findByEmail("diogo@teste.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Diogo");
    }

    @Test
    void naoDevePermitirEmailDuplicado() {
        userRepository.saveAndFlush(User.builder()
                .nome("A").email("dup@teste.com").senha("hash1").build());

        User user2 = User.builder()
                .nome("B").email("dup@teste.com").senha("hash2").build();

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(user2));
    }

    @Test
    void deveRetornarVazioQuandoEmailNaoExiste() {
        assertThat(userRepository.findByEmail("naoexiste@teste.com")).isEmpty();
    }
}