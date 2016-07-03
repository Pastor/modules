package ru.phi.modules.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.phi.modules.entity.User;

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
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            final Principal principal = webRequest.getUserPrincipal();
//            if (principal == null)
//                return null;
            if (authentication == null)
                throw new BadCredentialsException("Token empty");
            return ((AuthenticationWithToken) authentication).user;
        } else {
            return WebArgumentResolver.UNRESOLVED;
        }
    }
}
