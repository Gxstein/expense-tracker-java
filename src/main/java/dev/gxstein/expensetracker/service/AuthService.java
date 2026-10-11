package dev.gxstein.expensetracker.service;

import dev.gxstein.expensetracker.api.dto.LoginRequest;
import dev.gxstein.expensetracker.api.dto.RegisterRequest;
import dev.gxstein.expensetracker.api.dto.TokenResponse;
import dev.gxstein.expensetracker.domain.User;
import dev.gxstein.expensetracker.repository.UserRepository;
import dev.gxstein.expensetracker.service.exception.EmailAlreadyRegisteredException;
import dev.gxstein.expensetracker.service.exception.InvalidCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = users.save(new User(
                request.name().strip(),
                email,
                passwordEncoder.encode(request.password())));
        return jwtService.issueToken(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        User user = users.findByEmail(email)
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return jwtService.issueToken(user);
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
