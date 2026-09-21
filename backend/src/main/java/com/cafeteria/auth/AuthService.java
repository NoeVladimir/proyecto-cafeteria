package com.cafeteria.auth;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Map<String, Long> sessions = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String name = normalize(request.name());
        String email = normalize(request.email()).toLowerCase();
        validate(name, email, request.password());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya esta registrado");
        }

        User user = userRepository.save(new User(name, email, passwordEncoder.encode(request.password())));
        return toResponse(user, createSession(user));
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String email = normalize(request.email()).toLowerCase();
        if (email.isBlank() || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo y contrasena son obligatorios");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .filter(candidate -> passwordEncoder.matches(request.password(), candidate.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
        return toResponse(user, createSession(user));
    }

    public User requireUser(String token) {
        Long id = sessions.get(token);
        if (id == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida");
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion no valida"));
    }

    public void logout(String token) { if (token != null) sessions.remove(token); }

    private String createSession(User user) {
        String token = UUID.randomUUID().toString();
        if (user.getId() != null) sessions.put(token, user.getId());
        return token;
    }

    private AuthDtos.AuthResponse toResponse(User user, String token) {
        return new AuthDtos.AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), token);
    }

    private void validate(String name, String email, String password) {
        if (name.isBlank() || email.isBlank() || password == null || password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nombre y correo son obligatorios; la contrasena debe tener al menos 8 caracteres");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
