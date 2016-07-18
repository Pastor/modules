package ru.phi.modules.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Объект не найден")
public final class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(Long id) {
        super(MessageFormat.format("Объект {0} не найден", id));
    }

    public ObjectNotFoundException() {
    }
}
