package com.diogo.dindin.service;

import com.diogo.dindin.dto.AuthResponse;
import com.diogo.dindin.dto.LoginRequest;
import com.diogo.dindin.dto.RegisterRequest;
import com.diogo.dindin.exception.CredenciaisInvalidasException;
import com.diogo.dindin.exception.EmailJaCadastradoException;
import com.diogo.dindin.model.User;
import com.diogo.dindin.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    // ---------- REGISTRO ----------

    @Test
    void deveRegistrarUsuarioComSenhaCriptografada() {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "senha123");

        when(userRepository.existsByEmail("diogo@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash-criptografado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(UUID.randomUUID());
            return u;
        });
        when(jwtService.gerarToken(any(User.class))).thenReturn("token-fake");

        AuthResponse response = authService.registrar(request);

        assertThat(response.token()).isEqualTo("token-fake");

        // Confirma que a senha salva no banco é o HASH, nunca o texto puro
        verify(userRepository).save(argThat(user ->
                user.getSenha().equals("hash-criptografado") &&
                        user.getEmail().equals("diogo@teste.com")
        ));
    }

    @Test
    void naoDeveRegistrarComEmailJaExistente() {
        RegisterRequest request = new RegisterRequest("Diogo", "diogo@teste.com", "senha123");

        when(userRepository.existsByEmail("diogo@teste.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(request))
                .isInstanceOf(EmailJaCadastradoException.class);

        // Garante que nunca tentou salvar nem gerar token nesse caso
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).gerarToken(any());
    }

    // ---------- LOGIN ----------

    @Test
    void deveLogarComCredenciaisCorretas() {
        LoginRequest request = new LoginRequest("diogo@teste.com", "senha123");

        User userSalvo = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash-criptografado")
                .build();

        when(userRepository.findByEmail("diogo@teste.com")).thenReturn(Optional.of(userSalvo));
        when(passwordEncoder.matches("senha123", "hash-criptografado")).thenReturn(true);
        when(jwtService.gerarToken(userSalvo)).thenReturn("token-fake");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("token-fake");
    }

    @Test
    void naoDeveLogarComSenhaErrada() {
        LoginRequest request = new LoginRequest("diogo@teste.com", "senhaErrada");

        User userSalvo = User.builder()
                .id(UUID.randomUUID())
                .nome("Diogo")
                .email("diogo@teste.com")
                .senha("hash-criptografado")
                .build();

        when(userRepository.findByEmail("diogo@teste.com")).thenReturn(Optional.of(userSalvo));
        when(passwordEncoder.matches("senhaErrada", "hash-criptografado")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class);

        verify(jwtService, never()).gerarToken(any());
    }

    @Test
    void naoDeveLogarComEmailInexistente() {
        LoginRequest request = new LoginRequest("naoexiste@teste.com", "qualquerSenha");

        when(userRepository.findByEmail("naoexiste@teste.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredenciaisInvalidasException.class);

        // Nunca deve chamar o encoder se o usuário nem existe
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}