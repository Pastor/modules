package ru.phi.modules.security;

import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
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

import static java.text.MessageFormat.format;

@SuppressWarnings("unused")
@Service("securityTokenService.v1")
class SecurityAuthenticateService implements AuthenticateService {

    private static final HashFunction hash = Hashing.goodFastHash(256);

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
            throw new AuthenticationException("Не установлен ключ доступа");
        final String tokenKey = opKey.get();
        final Token token = tokenRepository.findByKey(tokenKey);
        if (token == null)
            throw new AuthenticationException(format("Ключ \"{0}\" не зарегистрирован в сервисе", tokenKey));
        return token;
    }

    @Transactional
    @Override
    public Token authenticate(String username, String password, Set<String> scopes)
            throws AuthenticationException {
        final User user = userRepository.find(username, password);
        if (user == null)
            throw new AuthenticationException(format("Пользователь {0} не найден", username));
        final Token token = new Token();
        token.setExpiredAt(LocalDateTime.now().plus(365, ChronoUnit.DAYS));
        token.setUser(user);
        token.setKey(SecurityUtilities.generateTokenKey());
        processScope(token, user.getRole(), scopes);
        tokenRepository.save(token);
        user.getTokens().clear();
        user.getTokens().add(token);
        return token;
    }

    @Override
    public void updateToken(Token token, Set<String> scopes) {
        if (token != null) {
            final User user = token.getUser();
            processScope(token, user.getRole(), scopes);
            tokenRepository.save(token);
        }
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
