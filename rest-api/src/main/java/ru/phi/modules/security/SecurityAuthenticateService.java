package ru.phi.modules.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static java.text.MessageFormat.format;

@SuppressWarnings("unused")
@Service("securityTokenService.v1")
final class SecurityAuthenticateService implements AuthenticateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Transactional(readOnly = true)
    @Override
    public User authenticate(String key) throws AuthenticationException {
        Optional<String> opKey = Optional.ofNullable(key);
        if (!opKey.isPresent())
            throw new AuthenticationException("Empty token");
        final String tokenKey = opKey.get();
        final Token token = tokenRepository.findByKey(tokenKey);
        if (token == null)
            throw new AuthenticationException(format("Token \"{0}\" not found", tokenKey));
        return token.getUser();
    }

    @Transactional
    @Override
    public Token authenticate(String username, String password) throws AuthenticationException {
        final User user = userRepository.find(username, password);
        if (user == null)
            throw new AuthenticationException();
        if (user.getTokens() == null || user.getTokens().size() == 0) {
            final Token token = new Token();
            token.setExpiredAt(LocalDateTime.now().plus(365, ChronoUnit.DAYS));
            token.setUser(user);
            token.setKey(UUID.randomUUID().toString());
            tokenRepository.save(token);
            user.getTokens().add(token);
        }
        return user.getTokens().iterator().next();
    }
}
