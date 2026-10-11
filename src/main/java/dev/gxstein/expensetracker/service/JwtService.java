package dev.gxstein.expensetracker.service;

import dev.gxstein.expensetracker.api.dto.TokenResponse;
import dev.gxstein.expensetracker.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration tokenLifetime;
    private final Clock clock;

    public JwtService(
            JwtEncoder encoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration}") Duration tokenLifetime) {
        this(encoder, issuer, tokenLifetime, Clock.systemUTC());
    }

    JwtService(JwtEncoder encoder, String issuer, Duration tokenLifetime, Clock clock) {
        if (tokenLifetime.isNegative() || tokenLifetime.isZero()) {
            throw new IllegalArgumentException("JWT expiration must be positive.");
        }
        this.encoder = encoder;
        this.issuer = issuer;
        this.tokenLifetime = tokenLifetime;
        this.clock = clock;
    }

    public TokenResponse issueToken(User user) {
        Instant issuedAt = clock.instant();
        Instant expiresAt = issuedAt.plus(tokenLifetime);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getEmail())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("userId", user.getId())
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new TokenResponse(token, "Bearer", tokenLifetime.toSeconds());
    }
}
