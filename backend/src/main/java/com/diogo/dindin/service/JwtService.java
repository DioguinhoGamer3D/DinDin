package com.diogo.dindin.service;

import com.diogo.dindin.model.User;

public interface JwtService {
    String gerarToken(User user);
    boolean validarToken(String token);
    String extrairEmail(String token);
}