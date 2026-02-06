package com.salestonetech.salestone.service;

import com.salestonetech.salestone.controller.dto.AuthResponse;
import com.salestonetech.salestone.controller.dto.LoginRequest;
import com.salestonetech.salestone.controller.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class AuthService {

    private final RestClient restClient;
    private final String supabaseKey;

    public AuthService(@Value("${supabase.url}") String supabaseUrl,
                       @Value("${supabase.key}") String supabaseKey) {
        this.supabaseKey = supabaseKey;
        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Map<String, Object> body = Map.of(
            "email", request.email(),
            "password", request.password()
        );

        // Chama o endpoint /token do Supabase (GoTrue)
        Map response = restClient.post()
                .uri("/auth/v1/token?grant_type=password")
                .header("apikey", supabaseKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        return mapToAuthResponse(response);
    }

    public AuthResponse register(RegisterRequest request) {
        // Prepara metadados do usuário (opcional)
        Map<String, Object> userData = Map.of(
            "first_name", request.firstName() != null ? request.firstName() : "",
            "last_name", request.lastName() != null ? request.lastName() : ""
        );

        Map<String, Object> body = Map.of(
            "email", request.email(),
            "password", request.password(),
            "data", userData
        );

        // Chama o endpoint /signup do Supabase
        Map response = restClient.post()
                .uri("/auth/v1/signup")
                .header("apikey", supabaseKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);
        
        // Se o email confirmation estiver desligado, já retorna o token.
        // Se estiver ligado, o token pode vir nulo ou vir um user sem sessão.
        if (response != null && response.containsKey("access_token")) {
            return mapToAuthResponse(response);
        }
        
        return null; // Indica que o cadastro foi feito, mas talvez precise confirmar email
    }

    private AuthResponse mapToAuthResponse(Map response) {
        if (response == null) return null;
        return new AuthResponse(
            (String) response.get("access_token"),
            (String) response.get("refresh_token"),
            (String) response.get("token_type"),
            (Integer) response.get("expires_in")
        );
    }
}