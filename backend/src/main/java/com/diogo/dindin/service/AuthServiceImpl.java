package com.diogo.dindin.service;

import com.diogo.dindin.dto.AuthResponse;
import com.diogo.dindin.dto.LoginRequest;
import com.diogo.dindin.dto.RegisterRequest;
import com.diogo.dindin.exception.CredenciaisInvalidasException;
import com.diogo.dindin.exception.EmailJaCadastradoException;
import com.diogo.dindin.model.User;
import com.diogo.dindin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        User user = User.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .build();

        User userSalvo = userRepository.save(user);

        String token = jwtService.gerarToken(userSalvo);
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), user.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = jwtService.gerarToken(user);
        return new AuthResponse(token);
    }
}