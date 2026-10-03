package com.diogo.dindin.repository;

import com.diogo.dindin.model.Categoria;
import com.diogo.dindin.model.TipoTransacao;
import com.diogo.dindin.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CategoriaRepositoryTest extends IntegrationTestBase {

    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void deveListarCategoriasApenasDoUsuarioDono() {
        User user1 = criarUsuario("user1@teste.com");
        User user2 = criarUsuario("user2@teste.com");

        categoriaRepository.save(criarCategoria("Alimentação", TipoTransacao.DESPESA, user1));
        categoriaRepository.save(criarCategoria("Transporte", TipoTransacao.DESPESA, user1));
        categoriaRepository.save(criarCategoria("Salário", TipoTransacao.RECEITA, user2));

        List<Categoria> categoriasUser1 = categoriaRepository.findByUserId(user1.getId());

        assertThat(categoriasUser1).hasSize(2);
        assertThat(categoriasUser1)
                .extracting(Categoria::getNome)
                .containsExactlyInAnyOrder("Alimentação", "Transporte");
    }

    private User criarUsuario(String email) {
        return userRepository.save(User.builder()
                .nome("Teste").email(email).senha("hash").build());
    }

    private Categoria criarCategoria(String nome, TipoTransacao tipo, User dono) {
        return Categoria.builder()
                .nome(nome).tipo(tipo).user(dono).build();
    }
}
