package ru.phi.modules.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.phi.modules.entity.Token;

import java.util.Optional;

@SuppressWarnings("unused")
@Component("authorizedToken.v1")
class AuthorizedTokenHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthorizedToken.class) != null
                && parameter.getParameterType().equals(Token.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        if (this.supportsParameter(parameter)) {
            final Optional<Token> token = Utilities.currentToken();
            if (token.isPresent()) {
                return token.get();
            }
            throw new BadCredentialsException("Нет авторизации");
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }
}
