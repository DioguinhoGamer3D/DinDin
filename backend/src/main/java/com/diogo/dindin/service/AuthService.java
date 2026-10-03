package com.diogo.dindin.service;

import com.diogo.dindin.dto.AuthResponse;
import com.diogo.dindin.dto.LoginRequest;
import com.diogo.dindin.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}