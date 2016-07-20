package ru.phi.modules.security;

import ru.phi.modules.entity.UserRole;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthorizedScope {
    String[] scopes() default {};

    UserRole[] roles() default {
            UserRole.admin,
            UserRole.content,
            UserRole.device,
            UserRole.user
    };
}
