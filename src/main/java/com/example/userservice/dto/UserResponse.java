package com.example.userservice.dto;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        boolean enabled,
        Instant createdAt,
        Set<String> roles,
        Set<String> permissions
) {
}
