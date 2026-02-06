package com.salestonetech.salestone.controller;

import com.salestonetech.salestone.controller.dto.AuthResponse;
import com.salestonetech.salestone.controller.dto.LoginRequest;
import com.salestonetech.salestone.controller.dto.RegisterRequest;
import com.salestonetech.salestone.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        if (response == null) {
            // Retorna 200 OK sem corpo se o usuário foi criado mas precisa confirmar e-mail
            return ResponseEntity.ok().build(); 
        }
        return ResponseEntity.ok(response);
    }
}