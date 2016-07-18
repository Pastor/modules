package ru.phi.modules.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Ошибка доступа")
public final class AccessScopeException extends RuntimeException {
    public AccessScopeException(String message) {
        super(message);
    }
}
