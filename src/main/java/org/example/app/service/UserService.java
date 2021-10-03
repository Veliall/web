package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.dto.*;
import org.example.app.exception.*;
import org.example.app.repository.UserRepository;
import org.example.app.util.RolesConverter;
import org.example.framework.security.*;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final var token = (String) authentication.getPrincipal();
        if (!tokenIsAlive(token)) {
            throw new TokenInvalidException();
        }
        repository.updateTokenLifeTime(token);

        return repository.findByToken(token)
                .map(o -> new TokenAuthentication(o, null, getRoles(o.getId()), true))
                .orElseThrow(AuthenticationException::new);
    }

    @Override
    public Authentication authenticate(String username, String password) throws AuthenticationException {
        final var userWithPassword =
                repository.getByUsernameWithPassword(username)
                        .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, userWithPassword.getPassword())) {
            throw new PasswordNotMatchesException();
        }
        final var user = repository.getByUsername(username).orElseThrow(UserNotFoundException::new);
        return new BasicAuthentication(user, null, getRoles(user.getId()), true);
    }


    @Override
    public AnonymousAuthentication provide() {
        return new AnonymousAuthentication(new User(
                -1,
                "anonymous"
        ));
    }

    public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();
        final var hash = passwordEncoder.encode(password);
        final var token = keyGenerator.generateKey();
        final var saved = repository.save(0, username, hash).orElseThrow(RegistrationException::new);

        repository.saveToken(saved.getId(), token);
        return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();

        final var saved = repository.getByUsernameWithPassword(username)
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(password, saved.getPassword())) {
            throw new PasswordNotMatchesException();
        }

        final var token = keyGenerator.generateKey();

        if (repository.findToken(saved.getId()).isPresent()) {
            repository.updateToken(saved.getId(), token);
        } else {
            repository.saveToken(saved.getId(), token);
        }
        return new LoginResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public RecoveryResponseDto recoveryRequest(String username) {
        final var user = repository.getByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        String formatted = String.format("%05d", num);
        final var code = Integer.parseInt(formatted);

        if (repository.getCodeForRecovery(username).isEmpty()) {
            repository.recoveryRequest(username, code);
        } else {
            repository.recoveryUpdate(username, code);
        }

        return new RecoveryResponseDto(username, "Подтвердите код, для смены пароля");

    }

    public LoginResponseDto recovery(RecoveryRequestDto requestDto) {
        final var user = repository.getByUsername(requestDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        final var code = repository.getCodeForRecovery(requestDto.getUsername())
                .orElseThrow(WrongRecoveryCodeException::new);

        final var passwordEncoder = new Argon2PasswordEncoder();
        final User recoveredUser;
        if (code == requestDto.getCode()) {
            recoveredUser = repository.save(
                            user.getId(),
                            user.getUsername(),
                            passwordEncoder.encode(requestDto.getPassword()))
                    .orElseThrow(UserNotFoundException::new);
        } else {
            throw new WrongRecoveryCodeException();
        }
        return login(new LoginRequestDto(requestDto.getUsername(), requestDto.getPassword()));
    }

    public boolean tokenIsAlive(String token) {
        final var endTime = repository.getTokenEndLifeTime(token).orElseThrow(AuthenticationException::new);

        return endTime.toInstant().isAfter(Instant.now());
    }

    public List<String> getRoles(long id) {
        final var roles = repository.getRoles(id).orElseThrow(UserNotFoundException::new);
        return RolesConverter.readRoles(roles);
    }
}
