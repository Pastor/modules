package ru.phi.modules.security;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.api.AuthenticateService;
import ru.phi.modules.entity.Scope;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.User;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.exceptions.AuthenticationException;
import ru.phi.modules.repository.ScopeRepository;
import ru.phi.modules.repository.TokenRepository;
import ru.phi.modules.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.text.MessageFormat.format;

@SuppressWarnings("unused")
@Service("securityTokenService.v1")
class SecurityAuthenticateService implements AuthenticateService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Transactional(readOnly = true)
    @Override
    public Token authenticate(String key) throws AuthenticationException {
        Optional<String> opKey = Optional.ofNullable(key);
        if (!opKey.isPresent())
            throw new AuthenticationException("Empty token");
        final String tokenKey = opKey.get();
        final Token token = tokenRepository.findByKey(tokenKey);
        if (token == null)
            throw new AuthenticationException(format("Token \"{0}\" not found", tokenKey));
        return token;
    }

    @Transactional
    @Override
    public Token authenticate(String username, String password, boolean update) throws AuthenticationException {
        return authenticate(username, password, update, null);
    }

    @Override
    public Token authenticate(String username, String password, boolean update, Set<String> scopes)
            throws AuthenticationException {
        final User user = userRepository.find(username, password);
        if (user == null)
            throw new AuthenticationException();
        final Token token;
        if (user.getTokens() == null || user.getTokens().size() == 0) {
            token = new Token();
            token.setExpiredAt(LocalDateTime.now().plus(365, ChronoUnit.DAYS));
            token.setUser(user);
            token.setKey(UUID.randomUUID().toString());
            processScope(token, user.getRole(), scopes);
            tokenRepository.save(token);
            user.getTokens().add(token);
        } else if (update) {
            token = user.getTokens().iterator().next();
            token.setExpiredAt(LocalDateTime.now().plus(365, ChronoUnit.DAYS));
            processScope(token, user.getRole(), scopes);
            tokenRepository.save(token);
        } else if (user.getTokens().size() > 0) {
            token = user.getTokens().iterator().next();
        } else {
            throw new AuthenticationException("Неизвестное условие");
        }
        return token;
    }

    private void processScope(Token token, UserRole role, Set<String> scopes) {
        if (scopes == null || scopes.isEmpty())
            return;
        final Set<Scope> s = Sets.newHashSet();
        Set<Scope> ss = Sets.newHashSet(scopeRepository.findAll());
        for (String scope : scopes) {
            final Scope byName = scopeRepository.findByNameAndRole(scope, role);
            s.add(byName);
        }
        token.setScopes(s);
    }
}
