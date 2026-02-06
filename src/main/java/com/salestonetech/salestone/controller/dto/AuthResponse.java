package com.salestonetech.salestone.controller.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Integer expiresIn
) {}