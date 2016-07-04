package ru.phi.modules.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.phi.modules.entity.User;

import java.util.Optional;

@SuppressWarnings("unused")
@Component("authorizedUser.v1")
final class AuthorizedUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AuthorizedUser.class) != null
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        if (this.supportsParameter(parameter)) {
            final Optional<User> user = Utilities.currentUser();
            if (user.isPresent()) {
                return user.get();
            }
            throw new BadCredentialsException("Нет авторизации");
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }
}
