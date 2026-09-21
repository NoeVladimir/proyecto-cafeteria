package com.cafeteria.auth;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(String name, String email, String password) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record AuthResponse(Long id, String name, String email, User.Role role, String token) {
    }
}
