package ru.phi.modules.security;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthorizedScope {
    Scope[] scope() default {};
}
