package dev.gxstein.expensetracker.service;

import dev.gxstein.expensetracker.api.dto.RegisterRequest;
import dev.gxstein.expensetracker.api.dto.TokenResponse;
import dev.gxstein.expensetracker.domain.User;
import dev.gxstein.expensetracker.repository.UserRepository;
import dev.gxstein.expensetracker.service.exception.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository users;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService service;

    @Test
    void registerNormalizesEmailAndStoresOnlyEncodedPassword() {
        when(users.existsByEmail("person@example.com")).thenReturn(false);
        when(passwordEncoder.encode("correct-horse" )).thenReturn("encoded-value");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TokenResponse token = new TokenResponse("token", "Bearer", 3600);
        when(jwtService.issueToken(any(User.class))).thenReturn(token);

        TokenResponse result = service.register(
                new RegisterRequest("  Person  ", "  PERSON@example.com  ", "correct-horse"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        assertEquals("Person", captor.getValue().getName());
        assertEquals("person@example.com", captor.getValue().getEmail());
        assertEquals("encoded-value", captor.getValue().getPasswordHash());
        assertEquals(token, result);
    }

    @Test
    void registerRejectsDuplicateEmailBeforeSaving() {
        when(users.existsByEmail("person@example.com")).thenReturn(true);

        assertThrows(
                EmailAlreadyRegisteredException.class,
                () -> service.register(new RegisterRequest("Person", "PERSON@example.com", "password")));

        verify(users, never()).save(any(User.class));
    }
}
