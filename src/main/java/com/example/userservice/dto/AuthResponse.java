package com.example.userservice.dto;

public record AuthResponse(String tokenType, String accessToken, UserResponse user) {
}
