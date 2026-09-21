package com.cafeteria.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userRepository);
    }

    @Test
    void registerHashesPasswordAndReturnsUserData() {
        when(userRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthDtos.AuthResponse response = authService.register(
                new AuthDtos.RegisterRequest(" Ana ", "ANA@example.com", "secreto123"));

        assertThat(response.name()).isEqualTo("Ana");
        assertThat(response.email()).isEqualTo("ana@example.com");
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = new User("Ana", "ana@example.com", "$2a$10$invalid-hash");
        when(userRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.login(new AuthDtos.LoginRequest("ana@example.com", "incorrecta")));

        assertThat(exception.getStatusCode().value()).isEqualTo(401);
    }
}
