package ru.phi.modules.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Внутренняя ошибка сервиса")
public final class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }
}
