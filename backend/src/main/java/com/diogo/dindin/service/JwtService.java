package com.diogo.dindin.service;

import com.diogo.dindin.model.User;

import java.util.UUID;

public interface JwtService {
    String gerarToken(User user);
    boolean validarToken(String token);
    String extrairEmail(String token);
    UUID extrairUserId(String token);
}