package ru.phi.modules.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.phi.modules.entity.Token;
import ru.phi.modules.entity.UserRole;
import ru.phi.modules.exceptions.AccessScopeException;
import ru.phi.modules.repository.TokenRepository;

import java.util.Optional;
import java.util.Set;

import static java.text.MessageFormat.format;

@SuppressWarnings("unused")
@Aspect
@Component
final class AuthorizedScopeMonitor {

    @Autowired
    private TokenRepository repository;

    @Transactional
    @Around("execution(@ru.phi.modules.security.AuthorizedScope * *.*(..)) && @annotation(scope)")
    public Object scoped(ProceedingJoinPoint point, AuthorizedScope scope) throws Throwable {
        final Optional<Token> token = SecurityUtilities.currentToken();
        if (token.isPresent()) {
            final Token one = repository.findOne(token.get().getId());
            final Set<ru.phi.modules.entity.Scope> scopes = one.getScopes();
            for (String s : scope.scopes()) {
                final ru.phi.modules.entity.Scope ss = new ru.phi.modules.entity.Scope();
                ss.setName(s.toLowerCase());
                boolean found = false;
                for (UserRole scopeRole : scope.roles()) {
                    ss.setRole(scopeRole);
                    if (scopes.contains(ss)) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    throw new AccessScopeException(format("Область доступа \"{0}\" в ключе не зарегистрирована", s.toLowerCase()));
            }
        } else {
            throw new AccessScopeException("Ключ доступ не установлен");
        }
        return point.proceed();
    }
}
